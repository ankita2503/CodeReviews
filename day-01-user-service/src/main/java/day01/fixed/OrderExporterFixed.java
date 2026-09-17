package day01.fixed;

import day01.model.Order;
import day01.model.OrderRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

class OrderExporterFixed {

    private static final String[] HEADERS = {"id", "amount", "status"};

    private final OrderRepository orders;

    public OrderExporterFixed(OrderRepository orders) {
        this.orders = orders;
    }

    public void export(String customerId, String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(path), StandardCharsets.UTF_8)) {
            writeRow(writer, HEADERS);
            List<Order> list = getOrders(customerId);
            for (Order o : list) {
                writeRow(writer, o.getId() + "," + o.getAmount() + "," + o.getStatus());
            }
        }
    }

    public BigDecimal total(String customerId) {
        BigDecimal sum = BigDecimal.ZERO;
        List<Order> list = getOrders(customerId);
        for (Order o : list) {
            sum = sum.add(o.getAmount());
        }
        return sum;
    }

    private List<Order> getOrders(String customerId) {
        return orders.findByCustomer(customerId);
    }

    private void writeRow(BufferedWriter writer, String... fields) throws IOException {
        StringJoiner row = new StringJoiner(",");
        for (String field : fields) {
            row.add(escape(field));
        }
        writer.write(row.toString());
        writer.write("\r\n");
    }

    private String escape(String field) {
        if (field.contains("\"") || field.contains(",")
                || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
