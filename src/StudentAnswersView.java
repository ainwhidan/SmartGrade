import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentAnswersView extends JFrame {
    private int resultId;

    public StudentAnswersView(int resultId, String studentName, String quizTitle) {
        this.resultId = resultId;

        setTitle("Student Answers - " + studentName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Quiz: " + quizTitle + " - Student: " + studentName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Question", "Student Answer", "Correct Answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(400);

        loadAnswers(model);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadAnswers(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT q.question_text, sa.student_answer, q.correct_answer, sa.is_correct " +
                    "FROM student_answers sa " +
                    "JOIN questions q ON sa.question_id = q.question_id " +
                    "WHERE sa.result_id = ? " +
                    "ORDER BY q.question_id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, resultId);
            ResultSet rs = ps.executeQuery();

            int qNum = 1;
            while (rs.next()) {
                String question = qNum + ". " + rs.getString("question_text");
                String studentAns = rs.getString("student_answer");
                String correctAns = rs.getString("correct_answer");
                boolean isCorrect = rs.getBoolean("is_correct");

                String result = isCorrect ? "✓ Correct" : "✗ Wrong";

                model.addRow(new Object[]{
                        question,
                        studentAns != null ? studentAns : "Not answered",
                        correctAns,
                        result
                });

                qNum++;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading answers: " + e.getMessage());
        }
    }
}
