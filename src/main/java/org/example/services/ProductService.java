package org.example.services;

import org.example.DB.DBConnection;
import org.example.collections.ProductRepository;
import org.example.exceptions.InvalidDataException;
import org.example.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    public void addProduct(Product p) {
        if (p.getQuantity() < 0) {
            throw new InvalidDataException("Product quantity cannot be negative.");
        }
        if (p.getPrice() < 0) {
            throw new InvalidDataException("Product price cannot be negative.");
        }

        String sql = "INSERT INTO products(name, category, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());

            ps.executeUpdate();
            System.out.println("Product added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product p) {
        if (p.getQuantity() < 0) {
            throw new InvalidDataException("Product quantity cannot be negative.");
        }
        if (p.getPrice() < 0) {
            throw new InvalidDataException("Product price cannot be negative.");
        }

        String sql = "UPDATE products SET name=?, category=?, quantity=?, price=? WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getProductId());

            ps.executeUpdate();
            System.out.println("Product updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();
            System.out.println("Product deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product searchById(int id) {
        ProductRepository repo = new ProductRepository();
        return repo.getAllProducts().stream()
                .filter(p -> p.getProductId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchByName(String name) {
        ProductRepository repo = new ProductRepository();
        return repo.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Product> searchByCategory(String category) {
        ProductRepository repo = new ProductRepository();
        return repo.getAllProducts().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(java.util.stream.Collectors.toList());
    }
}
