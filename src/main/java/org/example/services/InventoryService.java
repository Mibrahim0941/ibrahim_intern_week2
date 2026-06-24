package org.example.services;

import org.example.DB.DBConnection;
import org.example.exceptions.InvalidDataException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryService {

    public void fixNegativeInventory() {
        String sql = "UPDATE products SET quantity = 0 WHERE quantity < 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("[System Initialization] Fixed " + updated + " products with negative inventory.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to fix negative inventory: " + e.getMessage());
        }
    }

    public void stockIn(int productId, int qty) {
        if (qty <= 0) {
            throw new InvalidDataException("Stock In quantity must be strictly greater than zero.");
        }

        String updateStock = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
        String insertLog = "INSERT INTO inventory_logs(product_id, action, quantity, log_time) VALUES (?, 'STOCK_IN', ?, NOW())";

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps1 = con.prepareStatement(updateStock);
            ps1.setInt(1, qty);
            ps1.setInt(2, productId);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(insertLog);
            ps2.setInt(1, productId);
            ps2.setInt(2, qty);
            ps2.executeUpdate();

            System.out.println("Stock IN successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stockOut(int productId, int qty) {
        if (qty <= 0) {
            throw new InvalidDataException("Stock Out quantity must be strictly greater than zero.");
        }

        String getQty = "SELECT quantity FROM products WHERE product_id = ?";
        String updateStock = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
        String insertLog = "INSERT INTO inventory_logs(product_id, action, quantity, log_time) VALUES (?, 'STOCK_OUT', ?, NOW())";

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps1 = con.prepareStatement(getQty);
            ps1.setInt(1, productId);

            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("quantity");
                if (currentQty < qty) {
                    System.out.println("Stock OUT failed: Not enough inventory!");
                    return;
                }

                PreparedStatement ps2 = con.prepareStatement(updateStock);
                ps2.setInt(1, qty);
                ps2.setInt(2, productId);
                ps2.executeUpdate();

                PreparedStatement ps3 = con.prepareStatement(insertLog);
                ps3.setInt(1, productId);
                ps3.setInt(2, qty);
                ps3.executeUpdate();

                System.out.println("Stock OUT successful!");
            } else {
                System.out.println("Product not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}