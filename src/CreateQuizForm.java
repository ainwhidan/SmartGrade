import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class CreateQuizForm extends JFrame {
    private JTextField titleField, durationField;
    private JComboBox<String> subjectBox, levelBox;
    private JPanel questionsPanel;
    private ArrayList<QuestionPanel> questionPanels;
    private QuizManagementForm parentForm;

    public CreateQuizForm(QuizManagementForm parentForm) {
        this.parentForm = parentForm;
        this.questionPanels = new ArrayList<>();

        setTitle("Create New Quiz");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("📝 Create New Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Main content with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Quiz Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Quiz Information"));
        infoPanel.setMaximumSize(new Dimension(650, 150));

        infoPanel.add(new JLabel("Quiz Title:"));
        titleField = new JTextField();
        infoPanel.add(titleField);

        infoPanel.add(new JLabel("Subject:"));
        String[] subjects = {"BM", "BI", "Math", "Science", "History", "PI/PM"};
        subjectBox = new JComboBox<>(subjects);
        infoPanel.add(subjectBox);

        infoPanel.add(new JLabel("Level (Form):"));
        String[] levels = {"1", "2", "3", "4", "5", "6"};
        levelBox = new JComboBox<>(levels);
        infoPanel.add(levelBox);

        infoPanel.add(new JLabel("Duration (minutes):"));
        durationField = new JTextField("30");
        infoPanel.add(durationField);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Questions Panel
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBorder(BorderFactory.createTitledBorder("Questions"));

        mainPanel.add(questionsPanel);

        // Add Question Button
        JButton addQuestionBtn = new JButton("➕ Add Question");
        addQuestionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addQuestionBtn.addActionListener(e -> addQuestionPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(addQuestionBtn);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton saveBtn = new JButton("Save Quiz");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setBackground(new Color(50, 205, 50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setPreferredSize(new Dimension(120, 35));

        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(128, 128, 128));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setPreferredSize(new Dimension(120, 35));

        saveBtn.addActionListener(e -> saveQuiz());
        cancelBtn.addActionListener(e -> dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Add first question AFTER window is visible
        SwingUtilities.invokeLater(() -> addQuestionPanel());
    }

    private void addQuestionPanel() {
        QuestionPanel qPanel = new QuestionPanel(questionPanels.size() + 1, this);
        questionPanels.add(qPanel);
        questionsPanel.add(qPanel);
        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    public void removeQuestionPanel(QuestionPanel panel) {
        questionPanels.remove(panel);
        questionsPanel.remove(panel);

        // Renumber remaining questions
        for (int i = 0; i < questionPanels.size(); i++) {
            questionPanels.get(i).setQuestionNumber(i + 1);
        }

        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    private void saveQuiz() {
        String title = titleField.getText().trim();
        String subject = (String) subjectBox.getSelectedItem();
        String level = (String) levelBox.getSelectedItem();
        String durationStr = durationField.getText().trim();

        // Validation
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quiz title!");
            return;
        }

        if (questionPanels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one question!");
            return;
        }

        int duration = 30;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid duration!");
            return;
        }

        // Validate all questions are filled
        for (int i = 0; i < questionPanels.size(); i++) {
            QuestionPanel qp = questionPanels.get(i);
            if (!qp.isValidQuestion()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields for Question " + (i + 1) + "!");
                return;
            }
        }

        // Calculate total marks
        int totalMarks = questionPanels.size(); // 1 mark per question

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert quiz
            String sql1 = "INSERT INTO quiz (teacher_id, title, subject, level, total_marks, duration) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, SessionManager.getLoggedInTeacherId());
            ps1.setString(2, title);
            ps1.setString(3, subject);
            ps1.setString(4, level);
            ps1.setInt(5, totalMarks);
            ps1.setInt(6, duration);
            ps1.executeUpdate();

            // Get generated quiz_id
            ResultSet rs = ps1.getGeneratedKeys();
            int quizId = 0;
            if (rs.next()) {
                quizId = rs.getInt(1);
            }

            // Insert questions
            String sql2 = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer, marks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);

            for (QuestionPanel qp : questionPanels) {
                ps2.setInt(1, quizId);
                ps2.setString(2, qp.getQuestionText());
                ps2.setString(3, qp.getOptionA());
                ps2.setString(4, qp.getOptionB());
                ps2.setString(5, qp.getOptionC());
                ps2.setString(6, qp.getOptionD());
                ps2.setString(7, qp.getCorrectAnswer());
                ps2.setInt(8, 1);
                ps2.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Quiz created successfully!");
            parentForm.loadQuizzes();
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner class for question panel
    class QuestionPanel extends JPanel {
        private int questionNumber;
        private JTextArea questionArea;
        private JTextField optionAField, optionBField, optionCField, optionDField;
        private JComboBox<String> correctAnswerBox;

        public QuestionPanel(int number, CreateQuizForm parent) {
            this.questionNumber = number;

            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setMaximumSize(new Dimension(650, 250));
            setPreferredSize(new Dimension(650, 250));

            // Header with question number and remove button
            JPanel headerPanel = new JPanel(new BorderLayout());
            JLabel qLabel = new JLabel("Question " + questionNumber);
            qLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerPanel.add(qLabel, BorderLayout.WEST);

            if (questionNumber > 1) {
                JButton removeBtn = new JButton("✖");
                removeBtn.setPreferredSize(new Dimension(40, 25));
                removeBtn.setBackground(new Color(220, 20, 60));
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.addActionListener(e -> parent.removeQuestionPanel(this));
                headerPanel.add(removeBtn, BorderLayout.EAST);
            }

            add(headerPanel, BorderLayout.NORTH);

            // Question content
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(6, 2, 5, 5));

            contentPanel.add(new JLabel("Question:"));
            questionArea = new JTextArea(2, 20);
            questionArea.setLineWrap(true);
            questionArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(questionArea);
            contentPanel.add(scrollPane);

            contentPanel.add(new JLabel("Option A:"));
            optionAField = new JTextField();
            contentPanel.add(optionAField);

            contentPanel.add(new JLabel("Option B:"));
            optionBField = new JTextField();
            contentPanel.add(optionBField);

            contentPanel.add(new JLabel("Option C:"));
            optionCField = new JTextField();
            contentPanel.add(optionCField);

            contentPanel.add(new JLabel("Option D:"));
            optionDField = new JTextField();
            contentPanel.add(optionDField);

            contentPanel.add(new JLabel("Correct Answer:"));
            String[] answers = {"A", "B", "C", "D"};
            correctAnswerBox = new JComboBox<>(answers);
            contentPanel.add(correctAnswerBox);

            add(contentPanel, BorderLayout.CENTER);
        }

        public void setQuestionNumber(int number) {
            this.questionNumber = number;
            JPanel headerPanel = (JPanel) getComponent(0);
            if (headerPanel != null && headerPanel.getComponentCount() > 0) {
                Component comp = headerPanel.getComponent(0);
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setText("Question " + number);
                }
            }
        }

        public boolean isValidQuestion() {
            try {
                return questionArea != null && !questionArea.getText().trim().isEmpty() &&
                        optionAField != null && !optionAField.getText().trim().isEmpty() &&
                        optionBField != null && !optionBField.getText().trim().isEmpty() &&
                        optionCField != null && !optionCField.getText().trim().isEmpty() &&
                        optionDField != null && !optionDField.getText().trim().isEmpty();
            } catch (Exception e) {
                return false;
            }
        }

        public String getQuestionText() {
            return questionArea != null ? questionArea.getText().trim() : "";
        }

        public String getOptionA() {
            return optionAField != null ? optionAField.getText().trim() : "";
        }

        public String getOptionB() {
            return optionBField != null ? optionBField.getText().trim() : "";
        }

        public String getOptionC() {
            return optionCField != null ? optionCField.getText().trim() : "";
        }

        public String getOptionD() {
            return optionDField != null ? optionDField.getText().trim() : "";
        }

        public String getCorrectAnswer() {
            return correctAnswerBox != null ? (String) correctAnswerBox.getSelectedItem() : "A";
        }
    }
}