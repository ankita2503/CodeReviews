package day01.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Simple stand-in so the exporter can be run and reviewed without a database. */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, List<Order>> ordersByCustomer = new HashMap<>();

    public void add(String customerId, Order order) {
        ordersByCustomer.computeIfAbsent(customerId, key -> new ArrayList<>()).add(order);
    }

    @Override
    public List<Order> findByCustomer(String customerId) {
        return ordersByCustomer.getOrDefault(customerId, List.of());
    }
}
