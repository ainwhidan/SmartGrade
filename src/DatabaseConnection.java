import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_tracker";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // kosong jika tiada password XAMPP

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
            return conn;
        } catch (Exception e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection(); // run test
    }
}

