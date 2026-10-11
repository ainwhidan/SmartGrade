import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainMenu() {
        initUI();
    }

    private void initUI() {
        setTitle("Teacher Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel with Teacher Name (Green)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34)); // Forest Green
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("👨‍🏫 Teacher Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Hi, " + SessionManager.getLoggedInTeacherName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel headerText = new JPanel(new GridLayout(2, 1));
        headerText.setBackground(new Color(34, 139, 34));
        headerText.add(titleLabel);
        headerText.add(welcomeLabel);


        JButton profileBtn = new JButton("👤");
        profileBtn.setFont(new Font("Arial", Font.PLAIN, 24));
        profileBtn.setPreferredSize(new Dimension(50, 50));
        profileBtn.setBackground(new Color(46, 184, 46)); // Lighter green
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setFocusPainted(false);
        profileBtn.setBorderPainted(false);
        profileBtn.setOpaque(true);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setToolTipText("View Profile");
        profileBtn.addActionListener(e -> new TeacherProfileForm());

        headerPanel.add(headerText, BorderLayout.WEST);
        headerPanel.add(profileBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Card Layout for different views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Dashboard Menu
        JPanel dashboardPanel = createDashboardPanel();
        mainPanel.add(dashboardPanel, "DASHBOARD");

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

        // Add Student Button
        JButton addStudentBtn = createDashboardButton("➕ Add Student", new Color(50, 205, 50));
        addStudentBtn.addActionListener(e -> new AddStudentForm());
        panel.add(addStudentBtn);

        panel.add(Box.createVerticalStrut(15));

        // View Students Button
        JButton viewStudentsBtn = createDashboardButton("👥 View Students", new Color(30, 144, 255));
        viewStudentsBtn.addActionListener(e -> new LevelSelectionForm());
        panel.add(viewStudentsBtn);

        panel.add(Box.createVerticalStrut(15));

        // Manage Quizzes Button
        JButton manageQuizzesBtn = createDashboardButton("📝 Manage Quizzes", new Color(255, 140, 0)); // Dark Orange
        manageQuizzesBtn.addActionListener(e -> new QuizManagementForm());
        panel.add(manageQuizzesBtn);

        panel.add(Box.createVerticalStrut(15));

        // Announcements Button
        JButton announcementsBtn = createDashboardButton("📢 Announcements", new Color(138, 43, 226)); // Blue Violet
        announcementsBtn.addActionListener(e -> new AnnouncementManagementForm());
        panel.add(announcementsBtn);

        panel.add(Box.createVerticalStrut(30));

        // Logout Button
        JButton logoutBtn = createDashboardButton("🚪 Log Out", new Color(220, 20, 60));
        logoutBtn.addActionListener(e -> {
            SessionManager.logout();
            dispose();
            new WelcomePage();
        });
        panel.add(logoutBtn);

        panel.add(Box.createVerticalStrut(15));

        // Exit Button
        JButton exitBtn = createDashboardButton("❌ Exit", new Color(128, 128, 128));
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}