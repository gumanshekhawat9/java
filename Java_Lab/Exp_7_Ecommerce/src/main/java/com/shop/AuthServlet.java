package com.shop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Handles /login, /register and /logout. */
@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            res.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        String message = "1".equals(req.getParameter("registered"))
                ? "<p class='ok'>Registration successful. Please log in.</p>" : "";
        showForm(req, res, "/login".equals(path), message);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if ("/login".equals(req.getServletPath())) {
            login(req, res);
        } else if ("/register".equals(req.getServletPath())) {
            register(req, res);
        } else {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email != null && password != null) {
            try (Connection con = Util.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT id, name, password_hash FROM users WHERE email = ?")) {
                ps.setString(1, email.trim().toLowerCase());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && Util.verify(password, rs.getString("password_hash"))) {
                        HttpSession session = req.getSession();
                        req.changeSessionId();   // new session id after login
                        session.setAttribute("userId", rs.getInt("id"));
                        session.setAttribute("userName", rs.getString("name"));
                        res.sendRedirect(req.getContextPath() + "/products");
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showForm(req, res, true, "<p class='err'>Database error. Is MySQL running?</p>");
                return;
            }
        }
        // Same message for wrong email and wrong password
        showForm(req, res, true, "<p class='err'>Invalid email or password.</p>");
    }

    private void register(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (name == null || name.isBlank() || email == null || !email.contains("@")
                || password == null || password.length() < 6) {
            showForm(req, res, false, "<p class='err'>Enter a name, a valid email and a password "
                    + "of at least 6 characters.</p>");
            return;
        }

        try (Connection con = Util.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO users(name, email, password_hash) VALUES(?,?,?)")) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, Util.hash(password));
            ps.executeUpdate();
            res.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (SQLIntegrityConstraintViolationException e) {
            showForm(req, res, false, "<p class='err'>That email is already registered.</p>");
        } catch (SQLException e) {
            e.printStackTrace();
            showForm(req, res, false, "<p class='err'>Database error. Is MySQL running?</p>");
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res, boolean isLogin,
                          String message) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String base = req.getContextPath();

        Layout.header(out, req, isLogin ? "Login" : "Register");
        out.println("<div class='box'><h2>" + (isLogin ? "Login" : "Create an account") + "</h2>");
        out.println(message);
        out.println("<form method='post' action='" + base + (isLogin ? "/login" : "/register") + "'>");
        if (!isLogin) {
            out.println("Name<input name='name' required>");
        }
        out.println("Email<input type='email' name='email' required>");
        out.println("Password<input type='password' name='password' required>");
        out.println("<button>" + (isLogin ? "Login" : "Register") + "</button></form>");
        out.println(isLogin
                ? "<p>New here? <a href='" + base + "/register'>Create an account</a></p>"
                : "<p>Already registered? <a href='" + base + "/login'>Login</a></p>");
        out.println("</div>");
        Layout.footer(out);
    }
}
