package com.shop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute("userId");
        if (userId == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        Layout.header(out, req, "My Orders");
        out.println("<h1>My Orders</h1>");

        String sql = "SELECT o.id AS order_id, o.total, o.created_at, p.name, i.quantity, i.price "
                + "FROM orders o JOIN order_items i ON i.order_id = o.id "
                + "JOIN products p ON p.id = i.product_id "
                + "WHERE o.user_id = ? ORDER BY o.id DESC, i.id";

        try (Connection con = Util.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int currentOrder = -1;
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int orderId = rs.getInt("order_id");
                    if (orderId != currentOrder) {
                        if (currentOrder != -1) {
                            out.println("</table><br>");
                        }
                        currentOrder = orderId;
                        out.println("<h3>Order #" + orderId + " &nbsp; <span class='muted'>"
                                + rs.getTimestamp("created_at") + "</span> &nbsp; Total: "
                                + Util.money(rs.getBigDecimal("total")) + "</h3>");
                        out.println("<table><tr><th>Product</th><th>Qty</th><th>Price</th></tr>");
                    }
                    out.println("<tr><td>" + Util.escape(rs.getString("name")) + "</td><td>"
                            + rs.getInt("quantity") + "</td><td>"
                            + Util.money(rs.getBigDecimal("price")) + "</td></tr>");
                }
                if (any) {
                    out.println("</table>");
                } else {
                    out.println("<p>You have not placed any orders yet.</p>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p class='err'>Could not load your orders.</p>");
        }
        Layout.footer(out);
    }
}
