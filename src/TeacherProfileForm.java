import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TeacherProfileForm extends JFrame {
    private JLabel photoLabel;
    private JLabel nameLabel, phoneLabel, emailLabel, addressLabel;
    private int teacherId;

    public TeacherProfileForm() {
        this.teacherId = SessionManager.getLoggedInTeacherId();

        setTitle("Teacher Profile");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header Panel with Photo
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Photo placeholder
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(120, 120));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.WHITE);

        try {
            ImageIcon defaultIcon = new ImageIcon("src/profile_default.png");
            if (defaultIcon.getIconWidth() > 0) {
                Image img = defaultIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
            } else {
                photoLabel.setText("👤");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }
        } catch (Exception e) {
            photoLabel.setText("👤");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(photoLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel("Teacher Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1, 10, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        infoPanel.setBackground(Color.WHITE);

        nameLabel = createInfoLabel("Name:", "");
        phoneLabel = createInfoLabel("Phone:", "");
        emailLabel = createInfoLabel("Email:", "");
        addressLabel = createInfoLabel("Address:", "");

        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(addressLabel);

        add(infoPanel, BorderLayout.CENTER);

        // Buttons Panel (Removed Change Password button)
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton editBtn = new JButton("Edit Profile");
        JButton deleteBtn = new JButton("Delete Account");
        JButton closeBtn = new JButton("Close");

        styleButton(editBtn, new Color(30, 144, 255));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(closeBtn, new Color(128, 128, 128));

        editBtn.addActionListener(e -> openEditProfile());
        deleteBtn.addActionListener(e -> deleteAccount());
        closeBtn.addActionListener(e -> dispose());

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(closeBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        loadProfileData();
        setVisible(true);
    }

    private JLabel createInfoLabel(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b><br/>" + value + "</html>");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
    }

    private void loadProfileData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT full_name, phone, email, address FROM users WHERE teacherid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");

                nameLabel.setText("<html><b>Name:</b><br/>" + (name != null ? name : "N/A") + "</html>");
                phoneLabel.setText("<html><b>Phone:</b><br/>" + (phone != null && !phone.isEmpty() ? phone : "Not set") + "</html>");
                emailLabel.setText("<html><b>Email:</b><br/>" + (email != null && !email.isEmpty() ? email : "Not set") + "</html>");
                addressLabel.setText("<html><b>Address:</b><br/>" + (address != null && !address.isEmpty() ? address : "Not set") + "</html>");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void openEditProfile() {
        new EditTeacherProfileForm(this);
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?\nThis will also delete ALL your students and quizzes!",
                "Confirm Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Ask for password confirmation
            String password = JOptionPane.showInputDialog(this,
                    "Enter your password to confirm deletion:",
                    "Confirm Password",
                    JOptionPane.WARNING_MESSAGE);

            if (password == null || password.trim().isEmpty()) {
                return; // User cancelled
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Verify password
                String sqlVerify = "SELECT password FROM users WHERE teacherid = ?";
                PreparedStatement psVerify = conn.prepareStatement(sqlVerify);
                psVerify.setInt(1, teacherId);
                ResultSet rs = psVerify.executeQuery();

                if (rs.next()) {
                    String dbPassword = rs.getString("password");

                    if (!dbPassword.equals(password.trim())) {
                        JOptionPane.showMessageDialog(this, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Delete all students
                    String sql1 = "DELETE FROM student WHERE teacherid = ?";
                    PreparedStatement ps1 = conn.prepareStatement(sql1);
                    ps1.setInt(1, teacherId);
                    ps1.executeUpdate();

                    // Delete teacher account
                    String sql2 = "DELETE FROM users WHERE teacherid = ?";
                    PreparedStatement ps2 = conn.prepareStatement(sql2);
                    ps2.setInt(1, teacherId);
                    int result = ps2.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Account deleted successfully!");
                        SessionManager.logout();
                        dispose();
                        // Close all windows and return to welcome page
                        for (Window window : Window.getWindows()) {
                            window.dispose();
                        }
                        new WelcomePage();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting account!");
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            }
        }
    }

    public void refreshProfile() {
        loadProfileData();
    }
}