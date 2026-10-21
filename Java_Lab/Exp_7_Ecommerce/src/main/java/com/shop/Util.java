package com.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jakarta.servlet.http.HttpSession;

/** Database connection, password hashing, HTML escaping and cart helpers. */
public class Util {

    private static final String URL = "jdbc:mysql://localhost:3306/shop";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found in WEB-INF/lib");
        }
    }

    private Util() {}

    // Credentials come from environment variables (defaults are for local lab use).
    public static Connection getConnection() throws SQLException {
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");
        return DriverManager.getConnection(URL,
                user == null ? "root" : user,
                pass == null ? "root" : pass);
    }

    // Escape user-supplied text before printing it into HTML (prevents XSS).
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                   .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String money(BigDecimal amount) {
        return "&#8377;" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    // The cart is a map of productId -> quantity stored in the HTTP session.
    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> cart(HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Passwords are stored as salt:PBKDF2 hash, never as plain text.
    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + pbkdf2(password, salt);
    }

    public static boolean verify(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        return MessageDigest.isEqual(pbkdf2(password, salt).getBytes(), parts[1].getBytes());
    }

    private static String pbkdf2(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                                          .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }
}
