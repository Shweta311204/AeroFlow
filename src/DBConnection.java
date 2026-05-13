import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Provides a single static method to obtain a JDBC connection
 * to the AeroFlow MySQL database.
 *
 * Setup (run once in MySQL):
 *   CREATE DATABASE aeroflow;
 *   USE aeroflow;
 *   CREATE TABLE flights (
 *       id            INT AUTO_INCREMENT PRIMARY KEY,
 *       flight_number VARCHAR(20)  NOT NULL,
 *       destination   VARCHAR(100) NOT NULL,
 *       departure_time VARCHAR(5)  NOT NULL,   -- "HH:MM"
 *       price         DOUBLE       NOT NULL
 *   );
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/aeroflow";
    private static final String USER     = "root";      // change if needed
    private static final String PASSWORD = "";          // change if needed

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add mysql-connector-j to WEB-INF/lib.", e);
        }
    }

    /** Returns a new JDBC connection. Caller is responsible for closing it. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
