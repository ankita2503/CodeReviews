package day01.original;

import day01.model.Order;
import day01.model.OrderRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Code under review. Intentionally unchanged - do not fix anything here.
 * Write your corrected version in {@code day01.fixed.Fixed}.
 */
class OrderExporter {

    private final OrderRepository orders;

    public OrderExporter(OrderRepository orders) {
        this.orders = orders;
    }

    public void export(String customerId, String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write("id,amount,status\n");
        List<Order> list = orders.findByCustomer(customerId);
        for (int i = 0; i <= list.size(); i++) {
            Order o = list.get(i);
            writer.write(o.getId() + "," + o.getAmount() + "," + o.getStatus() + "\n");
        }
        writer.close();
    }

    public double total(String customerId) {
        double sum = 0;
        for (Order o : orders.findByCustomer(customerId)) {
            sum += o.getAmount().doubleValue();
        }
        return sum;
    }
}
