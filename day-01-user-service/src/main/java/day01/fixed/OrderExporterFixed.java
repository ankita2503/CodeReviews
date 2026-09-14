package day01.fixed;

import day01.model.Order;
import day01.model.OrderRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * TODO: your reviewed + corrected version of day01.original goes here.
 * Starting point below is a verbatim copy of the original.
 */
class OrderExporterFixed {

    private final OrderRepository orders;

    public OrderExporterFixed(OrderRepository orders) {
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
            sum += o.getAmount();
        }
        return sum;
    }
}
