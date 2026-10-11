import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewQuizForm extends JFrame {
    private int quizId;

    public ViewQuizForm(int quizId) {
        this.quizId = quizId;

        setTitle("View Quiz");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadQuizDetails(mainPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(128, 128, 128, 128);
        closeBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadQuizDetails(JPanel panel) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get quiz info
            String sql1 = "SELECT * FROM quiz WHERE quiz_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, quizId);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                // Quiz header
                JLabel titleLabel = new JLabel(rs1.getString("title"));
                titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(titleLabel);

                panel.add(Box.createVerticalStrut(10));

                JLabel infoLabel = new JLabel("<html>" +
                        "<b>Subject:</b> " + rs1.getString("subject") + "<br>" +
                        "<b>Level:</b> Form " + rs1.getString("level") + "<br>" +
                        "<b>Total Marks:</b> " + rs1.getInt("total_marks") + "<br>" +
                        "<b>Duration:</b> " + rs1.getInt("duration") + " minutes" +
                        "</html>");
                infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(infoLabel);

                panel.add(Box.createVerticalStrut(20));
            }

            // Get questions
            String sql2 = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, quizId);
            ResultSet rs2 = ps2.executeQuery();

            int qNum = 1;
            while (rs2.next()) {
                JPanel questionPanel = new JPanel();
                questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
                questionPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionPanel.setMaximumSize(new Dimension(550, 200));

                JLabel qLabel = new JLabel("<html><b>Question " + qNum + ":</b> " +
                        rs2.getString("question_text") + "</html>");
                qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionPanel.add(qLabel);

                questionPanel.add(Box.createVerticalStrut(10));

                String correctAns = rs2.getString("correct_answer");

                JLabel optionA = new JLabel("A) " + rs2.getString("option_a") +
                        (correctAns.equals("A") ? " ✓" : ""));
                JLabel optionB = new JLabel("B) " + rs2.getString("option_b") +
                        (correctAns.equals("B") ? " ✓" : ""));
                JLabel optionC = new JLabel("C) " + rs2.getString("option_c") +
                        (correctAns.equals("C") ? " ✓" : ""));
                JLabel optionD = new JLabel("D) " + rs2.getString("option_d") +
                        (correctAns.equals("D") ? " ✓" : ""));

                if (correctAns.equals("A")) optionA.setForeground(new Color(0, 128, 0));
                if (correctAns.equals("B")) optionB.setForeground(new Color(0, 128, 0));
                if (correctAns.equals("C")) optionC.setForeground(new Color(0, 128, 0));
                if (correctAns.equals("D")) optionD.setForeground(new Color(0, 128, 0));

                optionA.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionB.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionC.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionD.setAlignmentX(Component.LEFT_ALIGNMENT);

                questionPanel.add(optionA);
                questionPanel.add(optionB);
                questionPanel.add(optionC);
                questionPanel.add(optionD);

                panel.add(questionPanel);
                panel.add(Box.createVerticalStrut(15));

                qNum++;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading quiz: " + e.getMessage());
        }
    }
}
