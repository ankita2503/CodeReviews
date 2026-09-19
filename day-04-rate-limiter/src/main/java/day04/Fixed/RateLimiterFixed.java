package day04.Fixed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Day 5 - the corrected rate limiter.
 *
 * The original was correct for one thread and wrong for many. Everything below
 * follows from a single decision: one user's state lives in ONE immutable object,
 * and that object is only ever swapped atomically.
 */
public class RateLimiterFixed {

    private static final int MAX_PER_MINUTE = 100;
    private static final long WINDOW_MS = 60_000L;

    /**
     * WAS: two HashMaps, `counts` and `timestamp`.
     *
     * Two problems. HashMap has no synchronisation at all, so concurrent puts can
     * corrupt its internal structure - lost entries, not just wrong numbers.
     *
     * And two maps can never be updated together: a thread can write `counts` and
     * be descheduled before it writes `timestamp`, leaving the pair permanently
     * inconsistent. One map holding one object per user makes that impossible.
     */
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    /**
     * A user's whole state as one immutable value. Immutability is what makes the
     * swap safe: no other thread can ever see a half-updated Window, because a
     * Window is never updated - only replaced.
     */
    private record Window(long startMs, int count) {
        boolean isExpired(long now) {
            return now - startMs >= WINDOW_MS;
        }
    }

    public boolean allow(String userId) {
        long now = System.currentTimeMillis();

        /*
         * WAS: get, then test, then put - three separate operations.
         *
         * THE BUG: two threads both read 99, both decide they are under the limit,
         * both write 100. Two requests allowed past the limit, and one increment
         * lost forever. This is "check-then-act", and it is the same shape as the
         * Day 1 cache bug.
         *
         * compute() runs this function while holding the lock for THIS KEY, so the
         * read and the write cannot be split apart. Other keys are unaffected, so
         * this does not serialise the whole map.
         *
         * Note the window check happens FIRST and unconditionally. Nesting it
         * inside the under-limit test makes the reset unreachable once a user hits
         * the cap - a limiter that becomes a permanent ban.
         */
        /*
         * The decision is recorded INSIDE the lambda, where the state is known and
         * locked. Deciding afterwards would mean reading the map a second time,
         * and another thread could change it in between - the race, reintroduced.
         *
         * A one-element array is the ordinary way to get a value out of a lambda,
         * since a local variable cannot be assigned from inside one.
         */
//        Why boolean[] instead of just boolean?
//        Because Java lambdas have an important restriction.
//        You cannot modify a local variable from inside a lambda:
          boolean[] allowed = new boolean[1];
//        But an array reference can remain unchanged while its contents change:

        windows.compute(userId, (key, value) -> {
            if (value == null || value.isExpired(now)) {
                allowed[0] = true;
                return new Window(now, 1);
            }
            if (value.count() >= MAX_PER_MINUTE) {
                allowed[0] = false;
                return value;              // at the cap: unchanged, request refused
            }
            allowed[0] = true;
            return new Window(value.startMs(), value.count() + 1);
        });

        return allowed[0];
    }

    /**
     * WAS: return counts.get(userId);
     *
     * Returned null for an unknown user, which unboxes to int and throws NPE.
     * The admin dashboard went down when it looked up a user who had not made a
     * request yet.
     */
    public int getCount(String userId) {
        Window w = windows.get(userId);
        if (w == null || w.isExpired(System.currentTimeMillis())) {
            return 0;
        }
        return w.count();
    }

    /**
     * WAS: nothing.
     *
     * The original map grew one entry per unique user id and was only ever emptied
     * by the global window reset. Per-user windows remove that reset, so without
     * eviction the map leaks forever. Call this periodically from a scheduled task.
     *
     * A production system would use a bounded cache such as Caffeine with
     * expireAfterWrite instead of hand-rolling this.
     */
    public void evictExpired() {
        long now = System.currentTimeMillis();
        windows.values().removeIf(w -> w.isExpired(now));
    }
}