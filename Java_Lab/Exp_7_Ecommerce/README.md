# TechKart: E-Commerce Web Application (Java Servlets + JDBC + MySQL)

A small online shop built with Jakarta Servlets, JDBC and MySQL, running on Apache Tomcat 11.

## Features
- Product catalogue with search
- Shopping cart stored in the HTTP session
- User registration and login (passwords hashed with PBKDF2 and a random salt)
- Checkout that saves the order in a single database transaction and reduces stock
- Order history for the logged-in user

## Concepts Used
- Servlets (`doGet`, `doPost`), URL mapping with `@WebServlet`, sessions
- JDBC with `PreparedStatement` (no SQL injection), transactions (`commit` / `rollback`)
- HTML output escaping (prevents XSS), session id change after login
- Maven WAR packaging, MySQL schema with foreign keys

## Project Structure
```
Exp_7_Ecommerce/
├── pom.xml
├── sql/schema.sql
├── README.md
└── src/main/
    ├── java/com/shop/
    │   ├── Util.java              (DB connection, hashing, escaping, cart helper)
    │   ├── Layout.java            (shared header, navigation, styles)
    │   ├── ProductsServlet.java   /products
    │   ├── CartServlet.java       /cart
    │   ├── AuthServlet.java       /login, /register, /logout
    │   ├── CheckoutServlet.java   /checkout
    │   └── OrdersServlet.java     /orders
    └── webapp/index.html
```

## Database
| Table | Columns |
|-------|---------|
| users | id, name, email (unique), password_hash |
| products | id, name, description, price, stock |
| orders | id, user_id, total, created_at |
| order_items | id, order_id, product_id, quantity, price |

## Setup
1. Install JDK 17+, Maven, MySQL and Apache Tomcat 11.
2. Run `sql/schema.sql` in MySQL (Workbench or the command line client). It creates the `shop` database and 8 sample products.
3. If your MySQL root password is not `root`, set it in the same terminal you use to start Tomcat:
   ```
   $env:DB_PASSWORD = "your_password"
   ```
4. Build: `mvn package`
5. Copy `target/shop.war` into Tomcat's `webapps` folder and start Tomcat with `bin\startup.bat`.
6. Open http://localhost:8080/shop/

## Pages
| URL | Purpose |
|-----|---------|
| `/products` | Browse and search products, add to cart |
| `/cart` | View cart, remove items, place order |
| `/register`, `/login`, `/logout` | Accounts |
| `/orders` | Order history (login required) |

## Possible Improvements
- Admin page to add and edit products
- Quantity update in the cart, product images
- CSRF tokens on forms, HTTPS, a database connection pool

## Author
Your Name, B.Tech CSE, GIT Jaipur
