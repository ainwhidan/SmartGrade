import javax.swing.*;
import java.awt.*;

public class WelcomePage extends JFrame {

    public WelcomePage() {
        // Set up the frame
        setTitle("Student Performance Tracker");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(173, 216, 230),
                        0, getHeight(), Color.WHITE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Top panel for logo and title
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Try to load logo, fallback to emoji
        try {
            ImageIcon icon = new ImageIcon("resources/logodep.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Image not found");
            }
        } catch (Exception e) {
            logoLabel.setText("📚");
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 100));
            logoLabel.setForeground(new Color(70, 130, 180));
        }

        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(logoLabel);
        topPanel.add(Box.createVerticalStrut(50));

        // Title
        JLabel titleLabel = new JLabel("SmartGRADE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage Student Records & Performance");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(105, 105, 105));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(subtitleLabel);

        // Version
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(versionLabel);

        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(80));

        // Login Button
        JButton loginButton = createStyledButton("LOGIN",
                new Color(70, 130, 180),
                new Color(100, 149, 237));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });
        centerPanel.add(loginButton);
        centerPanel.add(Box.createVerticalStrut(20));

        // Sign Up Button
        JButton signupButton = createStyledButton("SIGN UP (Teacher)",
                new Color(60, 179, 113),
                new Color(50, 205, 50));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.addActionListener(e -> {
            dispose();
            new RegisterForm();
        });
        centerPanel.add(signupButton);

        // Bottom panel for footer
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copyrightLabel = new JLabel("© 2025 Student Performance Tracker");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(128, 128, 128));
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel educationLabel = new JLabel("Made for Education");
        educationLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        educationLabel.setForeground(new Color(150, 150, 150));
        educationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(Box.createVerticalStrut(40));
        bottomPanel.add(copyrightLabel);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(educationLabel);

        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Make sure everything is visible
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint custom background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Draw border
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();

                // Paint the text (this is crucial!)
                super.paintComponent(g);
            }
        };

        // Set button properties
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // We're painting our own background
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(350, 55));
        button.setMaximumSize(new Dimension(350, 55));
        button.setMinimumSize(new Dimension(350, 55));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setMargin(new Insets(10, 20, 10, 20));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.repaint();
            }
        });

        return button;
    }

    public static void main(String[] args) {
        // Use invokeLater to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            new WelcomePage();
        });
    }
}