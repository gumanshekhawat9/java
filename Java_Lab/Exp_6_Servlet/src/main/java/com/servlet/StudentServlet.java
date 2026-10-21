package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String name = Html.escape(request.getParameter("name"));
        String email = Html.escape(request.getParameter("email"));
        String course = Html.escape(request.getParameter("course"));

        PrintWriter out = response.getWriter();
        out.println("<h1>Student Information</h1>");
        out.println("<p>Name: " + name + "</p>");
        out.println("<p>Email: " + email + "</p>");
        out.println("<p>Course: " + course + "</p>");
        out.println("<a href='student.html'>Back</a>");
    }
}