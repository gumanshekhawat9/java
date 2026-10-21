package com.shop;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Shared page header (with navigation and styles) and footer. */
public class Layout {

    private static final String CSS =
        "*{box-sizing:border-box}body{margin:0;font-family:Segoe UI,Arial,sans-serif;background:#f4f6f8;color:#222}"
        + "header{background:#1f2a44;color:#fff;display:flex;justify-content:space-between;align-items:center;"
        + "padding:12px 24px;flex-wrap:wrap;gap:8px}"
        + "header a{color:#fff;text-decoration:none;margin-left:16px}.brand{font-size:22px;font-weight:700;margin:0}"
        + "main{max-width:1000px;margin:24px auto;padding:0 16px}"
        + ".grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:16px}"
        + ".card{background:#fff;border-radius:8px;padding:16px;box-shadow:0 1px 4px rgba(0,0,0,.12)}"
        + ".card h3{margin:0 0 6px}.price{font-size:20px;font-weight:700;color:#0a7a3d}.muted{color:#666;font-size:14px}"
        + "button,.btn{background:#2b6cb0;color:#fff;border:0;border-radius:6px;padding:8px 14px;cursor:pointer;"
        + "text-decoration:none;display:inline-block;font-size:14px}button:disabled{background:#999}"
        + "button.link{background:none;color:#c0392b;padding:0}"
        + "table{width:100%;border-collapse:collapse;background:#fff;border-radius:8px;overflow:hidden}"
        + "th,td{padding:10px;text-align:left;border-bottom:1px solid #e3e6ea}.total{font-size:20px;font-weight:700}"
        + "input{padding:8px;width:100%;margin:4px 0 12px;border:1px solid #bbb;border-radius:6px}"
        + ".box{max-width:380px;background:#fff;padding:20px;border-radius:8px;box-shadow:0 1px 4px rgba(0,0,0,.12)}"
        + ".err{color:#c0392b}.ok{color:#0a7a3d}.search{display:flex;gap:8px;margin-bottom:16px}.search input{margin:0}";

    private Layout() {}

    public static void header(PrintWriter out, HttpServletRequest req, String title) {
        String base = req.getContextPath();
        HttpSession session = req.getSession(false);
        String userName = session == null ? null : (String) session.getAttribute("userName");
        int count = 0;
        if (session != null) {
            for (int qty : Util.cart(session).values()) {
                count += qty;
            }
        }

        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>" + Util.escape(title) + " | TechKart</title>");
        out.println("<style>" + CSS + "</style></head><body>");
        out.println("<header><a class='brand' href='" + base + "/products'>TechKart</a><nav>");
        out.println("<a href='" + base + "/products'>Products</a>");
        out.println("<a href='" + base + "/cart'>Cart (" + count + ")</a>");
        if (userName != null) {
            out.println("<a href='" + base + "/orders'>My Orders</a>");
            out.println("<a href='" + base + "/logout'>Logout (" + Util.escape(userName) + ")</a>");
        } else {
            out.println("<a href='" + base + "/login'>Login</a>");
            out.println("<a href='" + base + "/register'>Register</a>");
        }
        out.println("</nav></header><main>");
    }

    public static void footer(PrintWriter out) {
        out.println("</main></body></html>");
    }
}
