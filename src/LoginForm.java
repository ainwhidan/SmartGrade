import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login Form");
        setSize(400, 320); // Slightly increased height
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel with vertical box layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title label
        JLabel titleLabel = new JLabel("Login Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form panel for username and password
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        formPanel.setMaximumSize(new Dimension(350, 80));

        formPanel.add(new JLabel("Username / IC:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password (Teacher only):"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ========== FORGOT PASSWORD LINK ==========
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        forgotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotPanel.setMaximumSize(new Dimension(350, 25));

        // Forgot Password as clickable text
        JLabel forgotPasswordLabel = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(new Color(0, 102, 204));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ForgotPasswordForm();
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(0, 51, 153));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(0, 102, 204));
            }
        });
        forgotPanel.add(forgotPasswordLabel);

        mainPanel.add(forgotPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== LOGIN AND EXIT BUTTONS ==========
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonsPanel.setMaximumSize(new Dimension(250, 100));
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        loginBtn.addActionListener(this::handleLogin);
        exitBtn.addActionListener(e -> {
            dispose();
            new WelcomePage();
        });

        buttonsPanel.add(loginBtn);
        buttonsPanel.add(exitBtn);

        mainPanel.add(buttonsPanel);

        // Add everything to the frame
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username / IC!");
            return;
        }

        // Teacher login check
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT teacherid, full_name FROM users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 👇 STORE teacher ID and name in session
                int teacherId = rs.getInt("teacherid");
                String fullName = rs.getString("full_name");
                SessionManager.setLoggedInTeacher(teacherId, fullName);

                JOptionPane.showMessageDialog(this, "Welcome " + fullName + "!");
                dispose();
                new MainMenu();
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }

        // Student login check (IC only)
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM student WHERE ic=?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Welcome Student!");
                dispose();
                new StudentView(username);
                return;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Invalid Credential!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomePage::new);
    }
}