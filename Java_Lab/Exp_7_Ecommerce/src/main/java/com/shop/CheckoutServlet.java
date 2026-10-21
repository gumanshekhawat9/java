package com.shop;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String base = req.getContextPath();
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            res.sendRedirect(base + "/login");
            return;
        }
        Map<Integer, Integer> cart = Util.cart(session);
        if (cart.isEmpty()) {
            res.sendRedirect(base + "/cart");
            return;
        }

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        Layout.header(out, req, "Order");

        try (Connection con = Util.getConnection()) {
            int orderId = placeOrder(con, userId, cart);
            cart.clear();
            out.println("<h1>Order placed</h1>");
            out.println("<p class='ok'>Thank you! Your order number is <b>#" + orderId + "</b>.</p>");
            out.println("<a class='btn' href='" + base + "/orders'>View my orders</a>");
        } catch (IllegalStateException e) {
            out.println("<h1>Could not place order</h1><p class='err'>" + Util.escape(e.getMessage()) + "</p>");
            out.println("<a class='btn' href='" + base + "/cart'>Back to cart</a>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h1>Could not place order</h1><p class='err'>Database error. Please try again.</p>");
        }
        Layout.footer(out);
    }

    // Everything happens in one transaction: either the whole order is saved, or nothing is.
    // Prices and stock are read from the database, never trusted from the browser.
    private int placeOrder(Connection con, int userId, Map<Integer, Integer> cart) throws SQLException {
        con.setAutoCommit(false);
        try {
            BigDecimal total = BigDecimal.ZERO;
            Map<Integer, BigDecimal> prices = new LinkedHashMap<>();

            for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                int productId = item.getKey();
                int qty = item.getValue();

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT name, price, stock FROM products WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("A product in your cart no longer exists.");
                        }
                        if (rs.getInt("stock") < qty) {
                            throw new IllegalStateException("Not enough stock for " + rs.getString("name")
                                    + " (only " + rs.getInt("stock") + " left).");
                        }
                        BigDecimal price = rs.getBigDecimal("price");
                        prices.put(productId, price);
                        total = total.add(price.multiply(BigDecimal.valueOf(qty)));
                    }
                }
                try (PreparedStatement up = con.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE id = ?")) {
                    up.setInt(1, qty);
                    up.setInt(2, productId);
                    up.executeUpdate();
                }
            }

            int orderId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders(user_id, total) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setBigDecimal(2, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES(?,?,?,?)")) {
                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getKey());
                    ps.setInt(3, item.getValue());
                    ps.setBigDecimal(4, prices.get(item.getKey()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return orderId;
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        }
    }
}
