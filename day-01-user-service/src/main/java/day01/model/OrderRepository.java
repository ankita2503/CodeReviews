package day01.model;

import java.util.List;

public interface OrderRepository {

    List<Order> findByCustomer(String customerId);
}
