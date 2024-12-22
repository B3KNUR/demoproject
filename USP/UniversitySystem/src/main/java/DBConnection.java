import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties properties = new Properties();
                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                if (input == null) {
                    throw new RuntimeException("Properties file not found in the classpath");
                }
                properties.load(input);

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");

                System.out.println("Trying to database connection...");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect database");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
