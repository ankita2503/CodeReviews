package day04.original;

import java.util.HashMap;
import java.util.Map;

/**
 * Code under review. Intentionally unchanged - do not fix anything here.
 * Write your corrected version in {@code day04.fixed.RateLimiterFixed}.
 *
 * <p>The change under review is a fixed-window per-user rate limiter.
 */
public class RateLimiterOriginal {

    private static final int MAX_PER_MINUTE = 100;

    private final Map<String, Integer> counts = new HashMap<>();
    private long windowStart = System.currentTimeMillis();

    public boolean allow(String userId) {
        if (System.currentTimeMillis() - windowStart > 60_000) {
            counts.clear();
            windowStart = System.currentTimeMillis();
        }

        Integer current = counts.get(userId);
        if (current == null) {
            current = 0;
        }

        if (current >= MAX_PER_MINUTE) {
            return false;
        }

        counts.put(userId, current + 1);
        return true;
    }

    public int getCount(String userId) {
        return counts.get(userId);
    }
}
