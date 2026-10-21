package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();

        // Demo credentials for the lab only. Never hard-code real credentials.
        if ("admin".equals(username) && "1234".equals(password)) {
            HttpSession session = request.getSession();
            request.changeSessionId();   // new session id after login (good security practice)
            session.setAttribute("username", username);

            out.println("<h1>Login Successful</h1>");
            out.println("<a href='home'>Go to Home</a>");
        } else {
            out.println("<h1>Invalid Login</h1>");
            out.println("<a href='index.html'>Try again</a>");
        }
    }
}