import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QuizResultsForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int quizId;

    public QuizResultsForm(int quizId) {
        this.quizId = quizId;

        setTitle("Quiz Results");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Quiz Results", SwingConstants.CENTER);
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
        model.setColumnIdentifiers(new Object[]{"Student IC", "Student Name", "Score", "Total", "Percentage", "Grade", "Date"});

        table = new JTable(model);
        table.setRowHeight(25);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String sql = "SELECT result_id, student_ic, s.name FROM quiz_results qr " +
                                    "JOIN student s ON qr.student_ic = s.ic " +
                                    "WHERE qr.quiz_id = ? " +
                                    "ORDER BY qr.score DESC " +
                                    "LIMIT 1 OFFSET ?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setInt(1, quizId);
                            ps.setInt(2, row);
                            ResultSet rs = ps.executeQuery();

                            if (rs.next()) {
                                int resultId = rs.getInt("result_id");
                                String studentName = rs.getString("name");
                                String quizTitle = ((JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(0)).getText();
                                quizTitle = quizTitle.replace("Quiz Results", "").trim();

                                new StudentAnswersView(resultId, studentName, "Quiz");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(128, 128, 128, 128);
        closeBtn.addActionListener(e -> dispose());

        JButton viewAnswersBtn = new JButton("View Student Answers");
        viewAnswersBtn.setBounds(30,144,255,255);
        viewAnswersBtn.addActionListener(e -> viewSelectedStudentAnswers());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewAnswersBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadResults();
        setVisible(true);
    }
    private void viewSelectedStudentAnswers() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT qr.result_id, s.name, q.title " +
                    "FROM quiz_results qr " +
                    "JOIN student s ON qr.student_ic = s.ic " +
                    "JOIN quiz q ON qr.quiz_id = q.quiz_id " +
                    "WHERE qr.quiz_id = ? " +
                    "ORDER BY qr.score DESC, qr.completed_date ASC " +
                    "LIMIT 1 OFFSET ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            ps.setInt(2, selectedRow);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int resultId = rs.getInt("result_id");
                String studentName = rs.getString("name");
                String quizTitle = rs.getString("title");

                new StudentAnswersView(resultId, studentName, quizTitle);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadResults() {
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT qr.*, s.name " +
                    "FROM quiz_results qr " +
                    "JOIN student s ON qr.student_ic = s.ic " +
                    "WHERE qr.quiz_id = ? " +
                    "ORDER BY qr.score DESC, qr.completed_date ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ic = rs.getString("student_ic");
                String name = rs.getString("name");
                int score = rs.getInt("score");
                int total = rs.getInt("total_marks");
                double percentage = rs.getDouble("percentage");
                String grade = getGrade(percentage);
                String date = rs.getString("completed_date");

                model.addRow(new Object[]{ic, name, score, total, String.format("%.1f%%", percentage), grade, date});
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No students have taken this quiz yet.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
        }
    }

    private String getGrade(double percentage) {
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        if (percentage >= 40) return "E";
        return "F";
    }
}