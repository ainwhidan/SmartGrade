import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AnnouncementManagementForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public AnnouncementManagementForm() {
        setTitle("Announcements Management");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("📢 Announcements", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.setColumnIdentifiers(new Object[]{"ID", "Title", "Level", "Date"});

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JButton createBtn = new JButton("Create Announcement");
        JButton viewBtn = new JButton("View Details");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        styleButton(createBtn, new Color(50, 205, 50));
        styleButton(viewBtn, new Color(30, 144, 255));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(closeBtn, new Color(128, 128, 128));

        createBtn.setPreferredSize(new Dimension(180, 35));
        viewBtn.setPreferredSize(new Dimension(130, 35));
        deleteBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setPreferredSize(new Dimension(100, 35));

        createBtn.addActionListener(e -> createAnnouncement());
        viewBtn.addActionListener(e -> viewSelectedAnnouncement());
        deleteBtn.addActionListener(e -> deleteSelectedAnnouncement());
        closeBtn.addActionListener(e -> dispose());

        buttonsPanel.add(createBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(closeBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        loadAnnouncements();
        setVisible(true);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadAnnouncements() {
        model.setRowCount(0);
        int teacherId = SessionManager.getLoggedInTeacherId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM announcements WHERE teacher_id = ? ORDER BY created_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("announcement_id"),
                        rs.getString("title"),
                        "Form " + rs.getString("level"),
                        rs.getString("created_date")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No announcements yet. Create your first one!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading announcements: " + e.getMessage());
        }
    }

    private void createAnnouncement() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextArea messageArea = new JTextArea(5, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        String[] levels = {"All", "1", "2", "3", "4", "5", "6"};
        JComboBox<String> levelBox = new JComboBox<>(levels);

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Message:"));
        panel.add(messageScroll);
        panel.add(new JLabel("Level:"));
        panel.add(levelBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Announcement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String message = messageArea.getText().trim();
            String level = (String) levelBox.getSelectedItem();

            if (title.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO announcements (teacher_id, title, message, level) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, SessionManager.getLoggedInTeacherId());
                ps.setString(2, title);
                ps.setString(3, message);
                ps.setString(4, level);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Announcement created successfully!");
                loadAnnouncements();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error creating announcement: " + e.getMessage());
            }
        }
    }

    private void viewSelectedAnnouncement() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an announcement first!");
            return;
        }

        int announcementId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM announcements WHERE announcement_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, announcementId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String message = rs.getString("message");
                String level = rs.getString("level");
                String date = rs.getString("created_date");

                String display = String.format(
                        "Title: %s\n" +
                                "Level: Form %s\n" +
                                "Date: %s\n\n" +
                                "Message:\n%s",
                        title, level, date, message
                );

                JTextArea textArea = new JTextArea(display);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "Announcement Details", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading announcement: " + e.getMessage());
        }
    }

    private void deleteSelectedAnnouncement() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an announcement first!");
            return;
        }

        String title = (String) model.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete announcement: " + title + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int announcementId = (int) model.getValueAt(selectedRow, 0);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM announcements WHERE announcement_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, announcementId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Announcement deleted successfully!");
                loadAnnouncements();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting announcement: " + e.getMessage());
            }
        }
    }
}
