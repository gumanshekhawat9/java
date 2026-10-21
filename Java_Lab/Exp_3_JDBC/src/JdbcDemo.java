
import java.sql.*;

public class JdbcDemo {

    static final String URL = "jdbc:mysql://localhost:3306/college";
    static final String USER = "root";

    public static void main(String[] args) {
        // Password comes from an environment variable so it never sits in GitHub.
        // If DB_PASSWORD is not set, it falls back to "root" for local lab use.
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = "root";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found. Add the Connector/J JAR to lib/ and the classpath.");
            return;
        }

        // try-with-resources closes the connection automatically
        try (Connection con = DriverManager.getConnection(URL, USER, password)) {
            insertStudent(con, 101, "Rahul", "CSE");
            insertStudent(con, 102, "Amit", "IT");
            insertStudent(con, 103, "Neha", "ECE");
            displayStudents(con);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static void insertStudent(Connection con, int id, String name, String branch)
            throws SQLException {
        String sql = "insert into student(id, name, branch) values(?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, branch);
            int rows = ps.executeUpdate();
            System.out.println("Record Inserted (" + rows + " row)");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("ID " + id + " already exists, skipping insert");
        }
    }

    static void displayStudents(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("select * from student")) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " "
                        + rs.getString("name") + " "
                        + rs.getString("branch"));
            }
        }
    }
}

