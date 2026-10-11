import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QuizManagementForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public QuizManagementForm() {
        setTitle("Quiz Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("📝 Quiz Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Card Layout for switching views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Menu Panel (first view)
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, "MENU");

        // Quiz List Panel
        JPanel quizListPanel = createQuizListPanel();
        mainPanel.add(quizListPanel, "LIST");

        add(mainPanel, BorderLayout.CENTER);

        // Show menu first
        cardLayout.show(mainPanel, "MENU");

        setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel instructionLabel = new JLabel("What would you like to do?", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(instructionLabel);

        panel.add(Box.createVerticalStrut(30));

        // Create Quiz Button
        JButton createQuizBtn = createLargeButton("➕ Create New Quiz", new Color(50, 205, 50));
        createQuizBtn.addActionListener(e -> {
            new CreateQuizForm(QuizManagementForm.this);
            cardLayout.show(mainPanel, "LIST");
        });
        panel.add(createQuizBtn);

        panel.add(Box.createVerticalStrut(15));

        // View Quizzes Button
        JButton viewQuizzesBtn = createLargeButton("📋 View All Quizzes", new Color(30, 144, 255));
        viewQuizzesBtn.addActionListener(e -> {
            loadQuizzes();
            cardLayout.show(mainPanel, "LIST");
        });
        panel.add(viewQuizzesBtn);

        panel.add(Box.createVerticalStrut(15));

        // View Results Button
        JButton viewResultsBtn = createLargeButton("📊 View All Results", new Color(255, 165, 0));
        viewResultsBtn.addActionListener(e -> showAllResults());
        panel.add(viewResultsBtn);

        panel.add(Box.createVerticalStrut(30));

        // Back Button
        JButton backBtn = createLargeButton("← Back to Main Menu", new Color(128, 128, 128));
        backBtn.addActionListener(e -> dispose());
        panel.add(backBtn);

        return panel;
    }

    private JPanel createQuizListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Table
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.setColumnIdentifiers(new Object[]{"ID", "Title", "Subject", "Level", "Questions", "Total Marks", "Status"});

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JButton createBtn = new JButton("➕ Create New");
        JButton viewBtn = new JButton("👁 View Quiz");
        JButton resultsBtn = new JButton("📊 View Results");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton backBtn = new JButton("← Back");

        styleButton(createBtn, new Color(50, 205, 50));
        styleButton(viewBtn, new Color(30, 144, 255));
        styleButton(resultsBtn, new Color(255, 165, 0));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(backBtn, new Color(128, 128, 128));

        createBtn.setPreferredSize(new Dimension(130, 35));
        viewBtn.setPreferredSize(new Dimension(130, 35));
        resultsBtn.setPreferredSize(new Dimension(140, 35));
        deleteBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setPreferredSize(new Dimension(100, 35));

        createBtn.addActionListener(e -> new CreateQuizForm(this));
        viewBtn.addActionListener(e -> viewSelectedQuiz());
        resultsBtn.addActionListener(e -> viewQuizResults());
        deleteBtn.addActionListener(e -> deleteSelectedQuiz());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        buttonsPanel.add(createBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(resultsBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(backBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createLargeButton(String text, Color color) {
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

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(originalColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(originalColor);
            }
        });

        return btn;
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

    public void loadQuizzes() {
        model.setRowCount(0);
        int teacherId = SessionManager.getLoggedInTeacherId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT q.quiz_id, q.title, q.subject, q.level, q.total_marks, " +
                    "COUNT(qs.question_id) as question_count, " +
                    "COUNT(DISTINCT qr.student_ic) as students_taken " +
                    "FROM quiz q " +
                    "LEFT JOIN questions qs ON q.quiz_id = qs.quiz_id " +
                    "LEFT JOIN quiz_results qr ON q.quiz_id = qr.quiz_id " +
                    "WHERE q.teacher_id = ? " +
                    "GROUP BY q.quiz_id " +
                    "ORDER BY q.created_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int studentsTaken = rs.getInt("students_taken");
                String status = studentsTaken > 0 ? "Active (" + studentsTaken + " taken)" : "New";

                model.addRow(new Object[]{
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("subject"),
                        "Form " + rs.getString("level"),
                        rs.getInt("question_count"),
                        rs.getInt("total_marks"),
                        status
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No quizzes found!\n\nClick 'Create New' to create your first quiz.",
                        "No Quizzes",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + e.getMessage());
        }
    }

    private void viewSelectedQuiz() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz first!");
            return;
        }

        int quizId = (int) model.getValueAt(selectedRow, 0);
        new ViewQuizForm(quizId);
    }

    private void deleteSelectedQuiz() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz first!");
            return;
        }

        String quizTitle = (String) model.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete quiz: " + quizTitle + "?\n\nThis will also delete all results!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int quizId = (int) model.getValueAt(selectedRow, 0);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM quiz WHERE quiz_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, quizId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Quiz deleted successfully!");
                loadQuizzes();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting quiz: " + e.getMessage());
            }
        }
    }

    private void viewQuizResults() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz first!");
            return;
        }

        int quizId = (int) model.getValueAt(selectedRow, 0);
        new QuizResultsForm(quizId);
    }

    private void showAllResults() {
        // Create quiz selection dialog
        JFrame quizListFrame = new JFrame("Select Quiz to View Results");
        quizListFrame.setSize(700, 500);
        quizListFrame.setLocationRelativeTo(this);
        quizListFrame.setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("📊 Select Quiz to View Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        quizListFrame.add(titleLabel, BorderLayout.NORTH);

        // Table for quiz list
        DefaultTableModel quizModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quizModel.setColumnIdentifiers(new Object[]{"Quiz ID", "Quiz Title", "Subject", "Level", "Students Taken", "Avg Score"});

        JTable quizTable = new JTable(quizModel);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizTable.setRowHeight(25);
        quizTable.getColumnModel().getColumn(0).setMaxWidth(70);

        // Load quizzes with result statistics
        int teacherId = SessionManager.getLoggedInTeacherId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT q.quiz_id, q.title, q.subject, q.level, " +
                    "COUNT(DISTINCT qr.student_ic) as students_taken, " +
                    "AVG(qr.score) as avg_score, q.total_marks " +
                    "FROM quiz q " +
                    "LEFT JOIN quiz_results qr ON q.quiz_id = qr.quiz_id " +
                    "WHERE q.teacher_id = ? " +
                    "GROUP BY q.quiz_id " +
                    "HAVING students_taken > 0 " +
                    "ORDER BY q.created_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int quizId = rs.getInt("quiz_id");
                String title = rs.getString("title");
                String subject = rs.getString("subject");
                String level = "Form " + rs.getString("level");
                int studentsTaken = rs.getInt("students_taken");
                double avgScore = rs.getDouble("avg_score");
                int totalMarks = rs.getInt("total_marks");

                String avgDisplay = String.format("%.1f/%d", avgScore, totalMarks);

                quizModel.addRow(new Object[]{
                        quizId,
                        title,
                        subject,
                        level,
                        studentsTaken,
                        avgDisplay
                });
            }

            if (!hasResults) {
                JOptionPane.showMessageDialog(quizListFrame,
                        "No quiz results yet!\nStudents haven't taken any quizzes.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                quizListFrame.dispose();
                return;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(quizListFrame, "Error loading results: " + e.getMessage());
            quizListFrame.dispose();
            return;
        }

        JScrollPane scrollPane = new JScrollPane(quizTable);
        quizListFrame.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton viewResultsBtn = new JButton("View Student Results");
        viewResultsBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        viewResultsBtn.setBackground(new Color(30, 144, 255));
        viewResultsBtn.setForeground(Color.WHITE);
        viewResultsBtn.setFocusPainted(false);
        viewResultsBtn.setBorderPainted(false);
        viewResultsBtn.setOpaque(true);
        viewResultsBtn.setPreferredSize(new Dimension(180, 35));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        closeBtn.setBackground(new Color(128, 128, 128));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setPreferredSize(new Dimension(100, 35));

        viewResultsBtn.addActionListener(e -> {
            int selectedRow = quizTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(quizListFrame, "Please select a quiz first!");
                return;
            }

            int selectedQuizId = (int) quizModel.getValueAt(selectedRow, 0);
            new QuizResultsForm(selectedQuizId);
        });

        closeBtn.addActionListener(e -> quizListFrame.dispose());

        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(closeBtn);

        quizListFrame.add(buttonPanel, BorderLayout.SOUTH);

        quizListFrame.setVisible(true);
    }
}