# JDBC: Java Database Connectivity with MySQL

A Java program that connects to a MySQL database through JDBC, inserts student records with a `PreparedStatement`, and reads them back with a `Statement` and `ResultSet`.

## Aim
To connect a Java application to a MySQL database using JDBC and perform insert and select operations.

## Concepts Used
- JDBC driver (`com.mysql.cj.jdbc.Driver`) and `DriverManager`
- `Connection`, `PreparedStatement`, `Statement`, `ResultSet`
- Parameterised queries (`?` placeholders)
- try-with-resources for automatic cleanup
- Handling `SQLException` and duplicate primary keys

## Flow
```
Load Driver -> Connect -> Prepare SQL -> Bind Parameters -> Execute INSERT
            -> Execute SELECT -> Read rows from ResultSet -> Close connection
```

## Project Structure
```
Exp_3_JDBC/
├── src/JdbcDemo.java
├── sql/college.sql
├── lib/            (put the MySQL Connector/J JAR here)
└── README.md
```

## Setup
1. Install JDK 8+ and MySQL Server.
2. Download **MySQL Connector/J** (Platform Independent) from https://dev.mysql.com/downloads/connector/j/ and copy the `.jar` file into `lib/`.
3. Create the database and table by running `sql/college.sql` in MySQL.
4. If your MySQL root password is not `root`, set it first (PowerShell):
```
   $env:DB_PASSWORD = "your_password"
```

## How to Run
From the `Exp_3_JDBC` folder:
```
javac -cp "lib/*" -d out src/JdbcDemo.java
java -cp "out;lib/*" JdbcDemo
```
On Mac/Linux, use `:` instead of `;`.

## Sample Output
```
Record Inserted (1 row)
Record Inserted (1 row)
Record Inserted (1 row)
101 Rahul CSE
102 Amit IT
103 Neha ECE
```

## Common Errors
| Error | Cause | Fix |
|-------|-------|-----|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Connector/J JAR missing from classpath | Put the JAR in `lib/` and use `-cp "lib/*"` |
| `Communications link failure` | MySQL server not running | Start the MySQL service |
| `Access denied for user 'root'` | Wrong username or password | Set `DB_PASSWORD` correctly |
| `Unknown database 'college'` | Database not created | Run `sql/college.sql` |
| `Table 'student' doesn't exist` | Table not created | Run `sql/college.sql` |
| `Duplicate entry '101'` | ID already exists | Handled in code, the insert is skipped |

## Author
Your Name, B.Tech CSE, GIT Jaipur

| 3 | [JDBC with MySQL](Exp_3_JDBC) | JDBC, PreparedStatement, ResultSet, MySQL |