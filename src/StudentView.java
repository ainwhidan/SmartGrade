import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentView extends JFrame {
    private String ic;
    private String studentName;
    private String level;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public StudentView(String ic) {
        this.ic = ic;
        loadStudentInfo();
        initUI();
    }

    private void loadStudentInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, level FROM student WHERE ic = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ic);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                studentName = rs.getString("name");
                level = rs.getString("level");
            }
        } catch (SQLException e) {
            studentName = "Student";
            level = "1";
        }
    }

    private void initUI() {
        setTitle("Student Dashboard - " + studentName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📚 Student Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome, " + studentName + " (Form " + level + ")");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel headerText = new JPanel(new GridLayout(2, 1));
        headerText.setBackground(new Color(30, 144, 255));
        headerText.add(titleLabel);
        headerText.add(welcomeLabel);

        headerPanel.add(headerText, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Card Layout for different views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Dashboard Menu
        JPanel dashboardPanel = createDashboardPanel();
        mainPanel.add(dashboardPanel, "DASHBOARD");

        // Results Panel
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, "RESULTS");

        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "DASHBOARD");

        setVisible(true);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        panel.setBackground(Color.WHITE);

        JLabel menuLabel = new JLabel("What would you like to do?");
        menuLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(menuLabel);

        panel.add(Box.createVerticalStrut(30));

        // Results Button
        JButton resultsBtn = createDashboardButton("📊 View My Results", new Color(30, 144, 255));
        resultsBtn.addActionListener(e -> cardLayout.show(mainPanel, "RESULTS"));
        panel.add(resultsBtn);

        panel.add(Box.createVerticalStrut(15));

        // Quiz Button
        JButton quizBtn = createDashboardButton("📝 Take Quiz", new Color(50, 205, 50));
        quizBtn.addActionListener(e -> showAvailableQuizzes());
        panel.add(quizBtn);

        panel.add(Box.createVerticalStrut(15));

        // Announcements Button
        JButton announcementsBtn = createDashboardButton("📢 Announcements", new Color(255, 165, 0));
        announcementsBtn.addActionListener(e -> showAnnouncements());
        panel.add(announcementsBtn);

        panel.add(Box.createVerticalStrut(30));

        // Logout Button
        JButton logoutBtn = createDashboardButton("🚪 Log Out", new Color(220, 20, 60));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });
        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("📊 My Performance Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Subject Marks
        JPanel marksPanel = new JPanel(new BorderLayout());
        String[] columnNames1 = {"Subject", "Marks", "Grade"};
        JTable marksTable = new JTable(new DefaultTableModel(columnNames1, 0));
        marksTable.setRowHeight(25);
        loadSubjectMarks(marksTable);
        marksPanel.add(new JScrollPane(marksTable), BorderLayout.CENTER);

        // Tab 2: Quiz Results
        JPanel quizPanel = new JPanel(new BorderLayout());
        String[] columnNames2 = {"Quiz Title", "Subject", "Score", "Date"};
        JTable quizTable = new JTable(new DefaultTableModel(columnNames2, 0));
        quizTable.setRowHeight(25);
        loadQuizResults(quizTable);
        quizPanel.add(new JScrollPane(quizTable), BorderLayout.CENTER);

        tabbedPane.addTab("Subject Marks", marksPanel);
        tabbedPane.addTab("Quiz Results", quizPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        // Back Button
        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backBtn.setPreferredSize(new Dimension(180, 35));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createDashboardButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(400, 50));
        btn.setPreferredSize(new Dimension(400, 50));

        return btn;
    }

    private void loadSubjectMarks(JTable table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, subject1, grade1, subject2, grade2, subject3, grade3, " +
                    "subject4, grade4, subject5, grade5, subject6, grade6, total, rank " +
                    "FROM student WHERE ic = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ic);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"BM", rs.getInt("subject1"), rs.getString("grade1")});
                model.addRow(new Object[]{"BI", rs.getInt("subject2"), rs.getString("grade2")});
                model.addRow(new Object[]{"Math", rs.getInt("subject3"), rs.getString("grade3")});
                model.addRow(new Object[]{"Science", rs.getInt("subject4"), rs.getString("grade4")});
                model.addRow(new Object[]{"History", rs.getInt("subject5"), rs.getString("grade5")});
                model.addRow(new Object[]{"PI/PM", rs.getInt("subject6"), rs.getString("grade6")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading marks: " + e.getMessage());
        }
    }

    private void loadQuizResults(JTable table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT q.title, q.subject, qr.score, qr.total_marks, qr.completed_date " +
                    "FROM quiz_results qr " +
                    "JOIN quiz q ON qr.quiz_id = q.quiz_id " +
                    "WHERE qr.student_ic = ? " +
                    "ORDER BY qr.completed_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ic);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            while (rs.next()) {
                String title = rs.getString("title");
                String subject = rs.getString("subject");
                String score = rs.getInt("score") + "/" + rs.getInt("total_marks");
                String date = rs.getString("completed_date");

                model.addRow(new Object[]{title, subject, score, date});
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No quiz results yet", "-", "-", "-"});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading quiz results: " + e.getMessage());
        }
    }

    private void showAvailableQuizzes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql1 = "SELECT teacherid FROM student WHERE ic = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, ic);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                int teacherId = rs1.getInt("teacherid");

                String sql2 = "SELECT quiz_id, title, subject, total_marks FROM quiz WHERE teacher_id = ? AND level = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, teacherId);
                ps2.setString(2, level);
                ResultSet rs2 = ps2.executeQuery();

                java.util.List<String> quizList = new java.util.ArrayList<>();
                java.util.List<Integer> quizIds = new java.util.ArrayList<>();

                while (rs2.next()) {
                    int quizId = rs2.getInt("quiz_id");
                    String title = rs2.getString("title");
                    String subject = rs2.getString("subject");
                    int marks = rs2.getInt("total_marks");

                    String sql3 = "SELECT COUNT(*) FROM quiz_results WHERE quiz_id = ? AND student_ic = ?";
                    PreparedStatement ps3 = conn.prepareStatement(sql3);
                    ps3.setInt(1, quizId);
                    ps3.setString(2, ic);
                    ResultSet rs3 = ps3.executeQuery();

                    boolean alreadyTaken = false;
                    if (rs3.next() && rs3.getInt(1) > 0) {
                        alreadyTaken = true;
                    }

                    String display = title + " - " + subject + " (" + marks + " marks)" + (alreadyTaken ? " [Completed]" : "");
                    quizList.add(display);
                    quizIds.add(quizId);
                }

                if (quizList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No quizzes available at the moment.");
                    return;
                }

                String[] options = quizList.toArray(new String[0]);
                String selected = (String) JOptionPane.showInputDialog(
                        this,
                        "Select a quiz to take:",
                        "Available Quizzes",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (selected != null) {
                    int index = quizList.indexOf(selected);
                    int selectedQuizId = quizIds.get(index);

                    if (selected.contains("[Completed]")) {
                        JOptionPane.showMessageDialog(this, "You have already completed this quiz!");
                        return;
                    }

                    new TakeQuizForm(selectedQuizId, ic);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + e.getMessage());
        }
    }

    private void showAnnouncements() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT title, message, created_date FROM announcements WHERE level = ? OR level = 'All' ORDER BY created_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, level);
            ResultSet rs = ps.executeQuery();

            StringBuilder announcements = new StringBuilder();
            announcements.append("<html><body style='width: 450px; font-family: Arial;'>");
            announcements.append("<h2 style='color: #FF8C00;'>📢 Announcements</h2>");

            boolean hasAnnouncements = false;
            while (rs.next()) {
                hasAnnouncements = true;
                String title = rs.getString("title");
                String message = rs.getString("message");
                String date = rs.getString("created_date");

                announcements.append("<div style='border: 1px solid #ddd; padding: 10px; margin: 10px 0; border-radius: 5px;'>");
                announcements.append("<h3 style='color: #1E90FF; margin: 0;'>").append(title).append("</h3>");
                announcements.append("<p style='color: #888; font-size: 11px; margin: 5px 0;'>").append(date).append("</p>");
                announcements.append("<p style='margin: 10px 0;'>").append(message).append("</p>");
                announcements.append("</div>");
            }

            if (!hasAnnouncements) {
                announcements.append("<p style='color: #888;'>No announcements at the moment.</p>");
            }

            announcements.append("</body></html>");

            JLabel label = new JLabel(announcements.toString());
            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Announcements", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading announcements: " + e.getMessage());
        }
    }
}