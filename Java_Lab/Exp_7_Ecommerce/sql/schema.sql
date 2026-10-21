CREATE DATABASE IF NOT EXISTS shop;
USE shop;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- INSERT IGNORE + UNIQUE name means this file is safe to run more than once
INSERT IGNORE INTO products (name, description, price, stock) VALUES
 ('Wireless Mouse', '2.4 GHz wireless mouse with silent clicks', 499.00, 40),
 ('Mechanical Keyboard', 'Blue switches, backlit, full size', 2499.00, 25),
 ('USB-C Hub', '7-in-1 hub: HDMI, USB 3.0, SD card reader', 1299.00, 30),
 ('Laptop Stand', 'Adjustable aluminium stand', 899.00, 35),
 ('Bluetooth Headphones', 'Over-ear, 30-hour battery', 1999.00, 20),
 ('64GB Pen Drive', 'USB 3.0 flash drive', 549.00, 60),
 ('HD Webcam', '1080p webcam with built-in microphone', 1599.00, 15),
 ('Laptop Backpack', 'Water-resistant, fits up to 15.6 inch', 1199.00, 25);
