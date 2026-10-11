import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class TakeQuizForm extends JFrame {
    private int quizId;
    private String studentIc;
    private String quizTitle;
    private int totalMarks;
    private ArrayList<Question> questions;
    private int currentQuestionIndex = 0;
    private ArrayList<String> studentAnswers;

    private JLabel questionLabel;
    private JLabel questionNumberLabel;
    private ButtonGroup answerGroup;
    private JRadioButton optionA, optionB, optionC, optionD;
    private JButton nextBtn, prevBtn, submitBtn;

    public TakeQuizForm(int quizId, String studentIc) {
        this.quizId = quizId;
        this.studentIc = studentIc;
        this.questions = new ArrayList<>();
        this.studentAnswers = new ArrayList<>();

        loadQuizData();

        setTitle("Take Quiz - " + quizTitle);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel(quizTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        questionNumberLabel = new JLabel("", SwingConstants.CENTER);
        questionNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(questionNumberLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Question Panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionPanel.add(questionLabel);

        questionPanel.add(Box.createVerticalStrut(20));

        answerGroup = new ButtonGroup();

        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();

        optionA.setFont(new Font("Arial", Font.PLAIN, 14));
        optionB.setFont(new Font("Arial", Font.PLAIN, 14));
        optionC.setFont(new Font("Arial", Font.PLAIN, 14));
        optionD.setFont(new Font("Arial", Font.PLAIN, 14));

        answerGroup.add(optionA);
        answerGroup.add(optionB);
        answerGroup.add(optionC);
        answerGroup.add(optionD);

        optionA.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionB.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionC.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionD.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionPanel.add(optionA);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(optionB);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(optionC);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(optionD);

        add(questionPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        prevBtn = new JButton("← Previous");
        nextBtn = new JButton("Next →");
        submitBtn = new JButton("Submit Quiz");

        prevBtn.setBounds(128, 128, 128,128);

        nextBtn.setBounds(30, 144, 255, 255);

        submitBtn.setBounds(50, 205, 50, 50);

        prevBtn.addActionListener(e -> previousQuestion());
        nextBtn.addActionListener(e -> nextQuestion());
        submitBtn.addActionListener(e -> submitQuiz());

        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(submitBtn);

        add(navPanel, BorderLayout.SOUTH);

        // Initialize student answers
        for (int i = 0; i < questions.size(); i++) {
            studentAnswers.add("");
        }

        displayQuestion(0);
        setVisible(true);
    }

    private void loadQuizData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get quiz info
            String sql1 = "SELECT title, total_marks FROM quiz WHERE quiz_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, quizId);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                quizTitle = rs1.getString("title");
                totalMarks = rs1.getInt("total_marks");
            }

            // Get questions
            String sql2 = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, quizId);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                Question q = new Question(
                        rs2.getString("question_text"),
                        rs2.getString("option_a"),
                        rs2.getString("option_b"),
                        rs2.getString("option_c"),
                        rs2.getString("option_d"),
                        rs2.getString("correct_answer")
                );
                questions.add(q);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading quiz: " + e.getMessage());
        }
    }

    private void displayQuestion(int index) {
        currentQuestionIndex = index;
        Question q = questions.get(index);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionLabel.setText("<html>" + q.questionText + "</html>");

        optionA.setText("A) " + q.optionA);
        optionB.setText("B) " + q.optionB);
        optionC.setText("C) " + q.optionC);
        optionD.setText("D) " + q.optionD);

        // Clear selection
        answerGroup.clearSelection();

        // Set previous answer if exists
        String savedAnswer = studentAnswers.get(index);
        if (!savedAnswer.isEmpty()) {
            switch (savedAnswer) {
                case "A": optionA.setSelected(true); break;
                case "B": optionB.setSelected(true); break;
                case "C": optionC.setSelected(true); break;
                case "D": optionD.setSelected(true); break;
            }
        }

        // Update button visibility
        prevBtn.setEnabled(index > 0);

        if (index == questions.size() - 1) {
            nextBtn.setVisible(false);
            submitBtn.setVisible(true);
        } else {
            nextBtn.setVisible(true);
            submitBtn.setVisible(false);
        }
    }

    private void saveCurrentAnswer() {
        String answer = "";
        if (optionA.isSelected()) answer = "A";
        else if (optionB.isSelected()) answer = "B";
        else if (optionC.isSelected()) answer = "C";
        else if (optionD.isSelected()) answer = "D";

        studentAnswers.set(currentQuestionIndex, answer);
    }

    private void nextQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex < questions.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        }
    }

    private void previousQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    private void submitQuiz() {
        saveCurrentAnswer();

        // Check if all questions answered
        for (String answer : studentAnswers) {
            if (answer.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "You haven't answered all questions. Submit anyway?",
                        "Confirm Submit",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                break;
            }
        }

        // Calculate score
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (studentAnswers.get(i).equals(questions.get(i).correctAnswer)) {
                score++;
            }
        }


        // Save result to database
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String sql1 = "INSERT INTO quiz_results (quiz_id, student_ic, score, total_marks, percentage) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, quizId);
            ps1.setString(2, studentIc);
            ps1.setInt(3, score);
            ps1.setInt(4, totalMarks);
            ps1.setDouble(5, (score * 100.0) / totalMarks);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int resultId = 0;
            if (rs.next()) {
                resultId = rs.getInt(1);
            }

            String sql2 = "INSERT INTO student_answers (result_id, question_id, student_answer, is_correct) VALUES (?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);

            // Get question IDs
            String sql3 = "SELECT question_id FROM questions WHERE quiz_id = ? ORDER BY question_id";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ps3.setInt(1, quizId);
            ResultSet rs3 = ps3.executeQuery();

            int index = 0;
            while (rs3.next() && index < studentAnswers.size()) {
                int questionId = rs3.getInt("question_id");
                String studentAnswer = studentAnswers.get(index);
                boolean isCorrect = studentAnswer.equals(questions.get(index).correctAnswer);

                ps2.setInt(1, resultId);
                ps2.setInt(2, questionId);
                ps2.setString(3, studentAnswer.isEmpty() ? null : studentAnswer);
                ps2.setBoolean(4, isCorrect);
                ps2.executeUpdate();

                index++;
            }

            conn.commit();

            showResults(score,totalMarks);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting quiz: " + e.getMessage());
        }
    }

    private void showResults(int score, int total) {
        String message = String.format(
                "Quiz Submitted Successfully!\n\n" +
                        "Your Score: %d / %d",
                score, total
        );

        JOptionPane.showMessageDialog(this, message, "Quiz Completed", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private String getGrade(double percentage) {
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        if (percentage >= 40) return "E";
        return "F";
    }

    // Inner class for Question
    class Question {
        String questionText;
        String optionA, optionB, optionC, optionD;
        String correctAnswer;

        public Question(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }
    }
}
