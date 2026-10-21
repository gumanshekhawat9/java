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

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String base = req.getContextPath();
        String q = req.getParameter("q");
        boolean search = q != null && !q.isBlank();

        Layout.header(out, req, "Products");
        out.println("<h1>Products</h1>");
        out.println("<form class='search' method='get' action='" + base + "/products'>"
                + "<input name='q' placeholder='Search products' value='" + Util.escape(q) + "'>"
                + "<button>Search</button></form>");

        String sql = "SELECT id, name, description, price, stock FROM products"
                + (search ? " WHERE name LIKE ?" : "") + " ORDER BY id";

        try (Connection con = Util.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (search) {
                ps.setString(1, "%" + q.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                out.println("<div class='grid'>");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int stock = rs.getInt("stock");
                    out.println("<div class='card'><h3>" + Util.escape(rs.getString("name")) + "</h3>");
                    out.println("<p class='muted'>" + Util.escape(rs.getString("description")) + "</p>");
                    out.println("<p class='price'>" + Util.money(rs.getBigDecimal("price")) + "</p>");
                    if (stock > 0) {
                        out.println("<form method='post' action='" + base + "/cart'>"
                                + "<input type='hidden' name='action' value='add'>"
                                + "<input type='hidden' name='productId' value='" + rs.getInt("id") + "'>"
                                + "<button>Add to cart</button></form>");
                    } else {
                        out.println("<button disabled>Out of stock</button>");
                    }
                    out.println("</div>");
                }
                out.println("</div>");
                if (!any) {
                    out.println("<p>No products found.</p>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p class='err'>Could not load products. Check that MySQL is running "
                    + "and that sql/schema.sql has been executed.</p>");
        }
        Layout.footer(out);
    }
}
