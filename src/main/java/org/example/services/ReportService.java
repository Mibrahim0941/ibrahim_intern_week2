package org.example.services;

import org.example.models.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Character.toLowerCase;

public class ReportService {
    public void top5Expensive(List<Product> products) {
        products.stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .limit(5)
                .forEach(System.out::println);
    }

    public void groupByCategory(List<Product> products) {
        Map<String, List<Product>> grouped =
                products.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getCategory().toLowerCase()
                        ));

        grouped.forEach((category, list) -> {
            System.out.println("\nCategory: " + category);
            list.forEach(System.out::println);
        });
    }

    public void lowStock(List<Product> products) {
        products.stream()
                .filter(p -> p.getQuantity() < 10)
                .forEach(System.out::println);
    }

    public void inventoryValue(List<Product> products) {
        double totalValue = products.stream()
                .mapToDouble(p -> p.getQuantity() * p.getPrice())
                .sum();

        System.out.printf("Total Inventory Value: %.2f%n", totalValue);
    }
}
