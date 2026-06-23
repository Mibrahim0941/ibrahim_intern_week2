package org.example.services;

import org.example.DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderService {

    public boolean processOrder(int productId, int qty) {

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String stockSql =
                    "SELECT quantity FROM products WHERE product_id=?";

            PreparedStatement stockPs =
                    con.prepareStatement(stockSql);

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

            String orderSql =
                    "INSERT INTO orders(product_id,quantity,order_date,status) " +
                            "VALUES(?,?,NOW(),'COMPLETED')";

            PreparedStatement orderPs =
                    con.prepareStatement(orderSql);

            orderPs.setInt(1, productId);
            orderPs.setInt(2, qty);
            orderPs.executeUpdate();

            String updateSql =
                    "UPDATE products " +
                            "SET quantity = quantity - ? " +
                            "WHERE product_id=?";

            PreparedStatement updatePs =
                    con.prepareStatement(updateSql);

            updatePs.setInt(1, qty);
            updatePs.setInt(2, productId);

            updatePs.executeUpdate();

            String logSql =
                    "INSERT INTO inventory_logs" +
                            "(product_id,action,quantity,log_time)" +
                            "VALUES(?, 'STOCK_OUT', ?, NOW())";

            PreparedStatement logPs =
                    con.prepareStatement(logSql);

            logPs.setInt(1, productId);
            logPs.setInt(2, qty);

            logPs.executeUpdate();

            con.commit();

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