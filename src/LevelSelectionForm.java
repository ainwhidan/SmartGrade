import javax.swing.*;
import java.awt.*;

public class LevelSelectionForm extends JFrame {

    public LevelSelectionForm() {
        setTitle("Select Form/Standard");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Select Form/Standard to View Students", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(6, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        String[] levels = {"1", "2", "3", "4", "5", "6"};

        for (String level : levels) {
            JButton btn = new JButton("Form/Standard " + level);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.addActionListener(e -> {
                dispose();
                new ViewStudents(level); // Pass the selected level
            });
            buttonsPanel.add(btn);
        }

        add(buttonsPanel, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
