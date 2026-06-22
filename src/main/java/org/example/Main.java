package org.example;

import org.example.models.Product;
import org.example.services.ProductService;

public class Main {
    public static void main(String[] args) {
        ProductService service = new ProductService();

        // ADD
        service.addProduct(new Product(0, "Laptop", "Electronics", 10, 120000));

        // SEARCH
        System.out.println(service.searchById(1));

        // UPDATE
        service.updateProduct(new Product(1, "Gaming Laptop", "Electronics", 5, 150000));

        // DELETE
        service.deleteProduct(3);

        // SEARCH LISTS
        System.out.println(service.searchByName("Laptop"));
        System.out.println(service.searchByCategory("Electronics"));
    }
}