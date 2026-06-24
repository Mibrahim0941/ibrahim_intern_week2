package org.example.services;

import org.example.DB.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderService {
    InventoryService inventory = new InventoryService();
    public boolean processOrder(int productId, int qty) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            String stockSql = "SELECT quantity FROM products WHERE product_id=?";
            PreparedStatement stockPs = con.prepareStatement(stockSql);
            stockPs.setInt(1, productId);
            ResultSet rs = stockPs.executeQuery();

            if (!rs.next()) {
                con.rollback();
                return false;
            }

            int available = rs.getInt("quantity");
            if (available < qty) {
                con.rollback();
                return false;
            }
            inventory.stockOut(productId, qty);

            return true;

        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception ignored) {}
        }
    }
}