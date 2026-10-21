package com.shop;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String base = req.getContextPath();
        HttpSession session = req.getSession();
        Map<Integer, Integer> cart = Util.cart(session);

        Layout.header(out, req, "Your Cart");
        out.println("<h1>Your Cart</h1>");

        if (cart.isEmpty()) {
            out.println("<p>Your cart is empty. <a href='" + base + "/products'>Browse products</a></p>");
            Layout.footer(out);
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        out.println("<table><tr><th>Product</th><th>Price</th><th>Qty</th><th>Subtotal</th><th></th></tr>");
        try (Connection con = Util.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name, price FROM products WHERE id = ?")) {
            for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                ps.setInt(1, item.getKey());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        continue;
                    }
                    BigDecimal price = rs.getBigDecimal("price");
                    BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getValue()));
                    total = total.add(subtotal);
                    out.println("<tr><td>" + Util.escape(rs.getString("name")) + "</td><td>"
                            + Util.money(price) + "</td><td>" + item.getValue() + "</td><td>"
                            + Util.money(subtotal) + "</td><td>"
                            + "<form method='post' action='" + base + "/cart'>"
                            + "<input type='hidden' name='action' value='remove'>"
                            + "<input type='hidden' name='productId' value='" + item.getKey() + "'>"
                            + "<button class='link'>Remove</button></form></td></tr>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<tr><td colspan='5' class='err'>Could not load cart details.</td></tr>");
        }
        out.println("</table>");
        out.println("<p class='total'>Total: " + Util.money(total) + "</p>");

        if (session.getAttribute("userId") == null) {
            out.println("<p>Please <a href='" + base + "/login'>log in</a> to place your order.</p>");
        } else {
            out.println("<form method='post' action='" + base + "/checkout'><button>Place Order</button></form>");
        }
        out.println("<br><form method='post' action='" + base + "/cart'>"
                + "<input type='hidden' name='action' value='clear'><button class='link'>Clear cart</button></form>");
        Layout.footer(out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<Integer, Integer> cart = Util.cart(req.getSession());
        String action = req.getParameter("action");

        try {
            if ("clear".equals(action)) {
                cart.clear();
            } else {
                int id = Integer.parseInt(req.getParameter("productId"));
                if ("add".equals(action)) {
                    cart.merge(id, 1, (a, b) -> Math.min(a + b, 10));   // max 10 of one product
                } else if ("remove".equals(action)) {
                    cart.remove(id);
                }
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product");
            return;
        }
        res.sendRedirect(req.getContextPath() + "/cart");
    }
}
