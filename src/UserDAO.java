import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // ✅ Register Teacher (with full_name)
    public static boolean registerTeacher(String fullName, String username, String password, String securityQuestion, String securityAnswer) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO users (username, password, full_name, role, security_question, security_answer) VALUES (?, ?, ?, 'teacher',?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, securityQuestion);
            stmt.setString(5, securityAnswer);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error Register Teacher: " + e.getMessage());
            return false;
        }
    }

    // ✅ Validate Teacher Login
    public static boolean validateTeacherLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=? AND role='teacher'");
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error Login Teacher: " + e.getMessage());
            return false;

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}
