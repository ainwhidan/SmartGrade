import javax.swing.*;

public class RegisterForm extends JFrame {

    private JTextField usernameField, fullNameField;
    private JPasswordField passwordField;

    public RegisterForm() {
        setTitle("Sign Up Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 280);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setBounds(20, 20, 100, 25);
        add(lblFullName);

        fullNameField = new JTextField();
        fullNameField.setBounds(120, 20, 180, 25);
        add(fullNameField);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 60, 100, 25);
        add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(120, 60, 180, 25);
        add(usernameField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 100, 100, 25);
        add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 100, 180, 25);
        add(passwordField);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(120, 140, 100, 30);
        add(btnRegister);

        btnRegister.addActionListener(e -> registerTeacher());

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(120, 180, 100, 30);
        add(btnBack);

        btnBack.addActionListener(e -> {
            dispose();
            new WelcomePage();
        });

        setVisible(true);
    }

    private void registerTeacher() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length()<6){
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] questions = {
                "What is your mother's name?",
                "What was the name of your first pet?",
                "What city were you born in?",
                "What is your favorite food?",
                "What was your childhood nickname?"
        };

        String selectedQuestion = (String) JOptionPane.showInputDialog(
                this,
                "Choose a security question for password recovery:",
                "Security Question",
                JOptionPane.QUESTION_MESSAGE,
                null,
                questions,
                questions[0]
        );

        if (selectedQuestion == null) {
            return; // User cancelled
        }

        String answer = JOptionPane.showInputDialog(this, selectedQuestion, "Security Answer", JOptionPane.QUESTION_MESSAGE);

        if (answer == null || answer.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide an answer to the security question!");
            return;
        }

        boolean success = UserDAO.registerTeacher(fullName, username, password, selectedQuestion, answer.trim());

        if (success) {
            JOptionPane.showMessageDialog(this, "Teacher Registered Successfully!");
            dispose();
            new LoginForm();

        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed!");
        }
    }
}

