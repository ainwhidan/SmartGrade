import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class EditTeacherProfileForm extends JFrame {
    private JTextField nameField, phoneField, emailField;
    private JTextArea addressArea;
    private JLabel profilePicLabel;
    private String profilePicturePath = null;
    private int teacherId;
    private TeacherProfileForm parentForm;

    public EditTeacherProfileForm(TeacherProfileForm parentForm) {
        this.teacherId = SessionManager.getLoggedInTeacherId();
        this.parentForm = parentForm;

        setTitle("Edit Profile");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        JLabel titleLabel = new JLabel("Edit Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Form Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Profile Picture Section
        JPanel picPanel = new JPanel();
        picPanel.setLayout(new BoxLayout(picPanel, BoxLayout.Y_AXIS));
        picPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        profilePicLabel = new JLabel();
        profilePicLabel.setPreferredSize(new Dimension(120, 120));
        profilePicLabel.setMinimumSize(new Dimension(120, 120));
        profilePicLabel.setMaximumSize(new Dimension(120, 120));
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        profilePicLabel.setOpaque(true);
        profilePicLabel.setBackground(Color.WHITE);
        profilePicLabel.setText("No Image");
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton uploadBtn = new JButton("Upload Profile Picture");
        uploadBtn.setFocusPainted(false);
        uploadBtn.setPreferredSize(new Dimension(200, 30));
        uploadBtn.setMaximumSize(new Dimension(200, 30));
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.addActionListener(e -> uploadProfilePicture());

        picPanel.add(profilePicLabel);
        picPanel.add(Box.createVerticalStrut(10));
        picPanel.add(uploadBtn);

        mainPanel.add(picPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Form Fields Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        saveBtn.setBackground(new Color(50, 205, 50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setPreferredSize(new Dimension(140, 35));

        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(128, 128, 128));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setPreferredSize(new Dimension(100, 35));

        saveBtn.addActionListener(e -> saveProfile());
        cancelBtn.addActionListener(e -> dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        loadCurrentData();
        setVisible(true);
    }

    private void uploadProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            profilePicturePath = selectedFile.getAbsolutePath();

            // Display the image
            try {
                ImageIcon icon = new ImageIcon(profilePicturePath);
                Image img = icon.getImage();
                Image scaledImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                profilePicLabel.setIcon(new ImageIcon(scaledImg));
                profilePicLabel.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
            }
        }
    }

    private void loadCurrentData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT full_name, phone, email, address, profile_picture FROM users WHERE teacherid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("full_name"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                addressArea.setText(rs.getString("address"));

                // Load existing profile picture
                String existingPicPath = rs.getString("profile_picture");
                if (existingPicPath != null && !existingPicPath.isEmpty()) {
                    profilePicturePath = existingPicPath;
                    File imgFile = new File(existingPicPath);
                    if (imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(existingPicPath);
                        Image img = icon.getImage();
                        Image scaledImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        profilePicLabel.setIcon(new ImageIcon(scaledImg));
                        profilePicLabel.setText("");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET full_name = ?, phone = ?, email = ?, address = ?, profile_picture = ? WHERE teacherid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setString(5, profilePicturePath);
            ps.setInt(6, teacherId);

            int result = ps.executeUpdate();

            if (result > 0) {
                // Update session name
                SessionManager.setLoggedInTeacher(teacherId, name);

                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                parentForm.refreshProfile();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating profile!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}