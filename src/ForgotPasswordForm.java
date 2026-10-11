import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ForgotPasswordForm extends JFrame {
    private JTextField usernameField;
    private JTextField answerField;
    private JPasswordField newPasswordField, confirmPasswordField;
    private JLabel questionLabel;
    private JButton verifyBtn, resetBtn;
    private String username;
    private String securityQuestion;

    public ForgotPasswordForm() {
        setTitle("Forgot Password");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("🔐 Password Recovery", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Step 1: Username
        JPanel usernamePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        usernamePanel.add(new JLabel("Enter your username:"));
        usernameField = new JTextField();
        usernamePanel.add(usernameField);
        usernamePanel.setMaximumSize(new Dimension(400, 60));
        formPanel.add(usernamePanel);

        formPanel.add(Box.createVerticalStrut(10));

        // Step 2: Security Question (hidden initially)
        JPanel questionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        questionLabel = new JLabel("");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        questionPanel.add(questionLabel);
        answerField = new JTextField();
        answerField.setEnabled(false);
        questionPanel.add(answerField);
        questionPanel.setMaximumSize(new Dimension(400, 60));
        questionPanel.setVisible(false);
        formPanel.add(questionPanel);

        formPanel.add(Box.createVerticalStrut(10));

        // Step 3: New Password (hidden initially)
        JPanel passwordPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        passwordPanel.add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        newPasswordField.setEnabled(false);
        passwordPanel.add(newPasswordField);

        passwordPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setEnabled(false);
        passwordPanel.add(confirmPasswordField);

        passwordPanel.setMaximumSize(new Dimension(400, 100));
        passwordPanel.setVisible(false);
        formPanel.add(passwordPanel);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        verifyBtn = new JButton("Verify");
        verifyBtn.setFocusPainted(false);
        verifyBtn.setPreferredSize(new Dimension(150,35));
        verifyBtn.addActionListener(e -> verifyUsername());

        resetBtn = new JButton("Reset Password");
        resetBtn.setFocusPainted(false);
        resetBtn.setPreferredSize(new Dimension(150,35));
        resetBtn.setVisible(false);
        resetBtn.addActionListener(e -> resetPassword());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100,35));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(verifyBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(cancelBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void verifyUsername() {
        username = usernameField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT security_question FROM users WHERE username = ? AND role = 'teacher'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                securityQuestion = rs.getString("security_question");

                if (securityQuestion == null || securityQuestion.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No security question set for this account.\nPlease contact administrator.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Show security question
                questionLabel.setText(securityQuestion);
                JPanel questionPanel = (JPanel) questionLabel.getParent();
                questionPanel.setVisible(true);
                answerField.setEnabled(true);

                JPanel passwordPanel = (JPanel) newPasswordField.getParent();
                passwordPanel.setVisible(true);
                newPasswordField.setEnabled(true);
                confirmPasswordField.setEnabled(true);

                usernameField.setEnabled(false);
                verifyBtn.setVisible(false);
                resetBtn.setVisible(true);

                revalidate();
                repaint();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Username not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void resetPassword() {
        String answer = answerField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        // Validation
        if (answer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please answer the security question!");
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter new password!");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        // Verify security answer
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql1 = "SELECT security_answer FROM users WHERE username = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, username);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                String correctAnswer = rs.getString("security_answer");

                if (!answer.equalsIgnoreCase(correctAnswer)) {
                    JOptionPane.showMessageDialog(this,
                            "Incorrect answer to security question!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update password
                String sql2 = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setString(1, newPassword);
                ps2.setString(2, username);
                int result = ps2.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Password reset successfully!\nYou can now login with your new password.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error resetting password!");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
