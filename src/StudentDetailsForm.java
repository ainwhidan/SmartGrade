import javax.swing.*;
import java.awt.*;

public class StudentDetailsForm extends JFrame {

    public StudentDetailsForm(int studentId) {
        setTitle("Student Details");
        setSize(450, 550);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Get student data
        StudentModel s = StudentDAO.getStudentById(studentId);

        if (s == null) {
            JOptionPane.showMessageDialog(this, "Student not found!");
            dispose();
            return;
        }

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(6, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        headerPanel.add(new JLabel("IC Number: " + s.getIc()));
        headerPanel.add(new JLabel("Name: " + s.getName()));
        headerPanel.add(new JLabel("Teacher ID: " + s.getTeacherId()));
        headerPanel.add(new JLabel("Form/Standard: " + s.getLevel()));
        headerPanel.add(new JLabel("Rank in Class: " + s.getRank()));
        headerPanel.add(new JLabel("Total Marks: " + s.getTotal()));

        add(headerPanel, BorderLayout.NORTH);

        // Subjects Panel
        JPanel subjectsPanel = new JPanel();
        subjectsPanel.setLayout(new GridLayout(7, 1, 5, 5));
        subjectsPanel.setBorder(BorderFactory.createTitledBorder("Subject Performance"));

        subjectsPanel.add(new JLabel("Subject Breakdown:", SwingConstants.CENTER));
        subjectsPanel.add(new JLabel("BM: " + s.getSubject1() + " (" + s.getGrade1() + ")"));
        subjectsPanel.add(new JLabel("BI: " + s.getSubject2() + " (" + s.getGrade2() + ")"));
        subjectsPanel.add(new JLabel("Math: " + s.getSubject3() + " (" + s.getGrade3() + ")"));
        subjectsPanel.add(new JLabel("Science: " + s.getSubject4() + " (" + s.getGrade4() + ")"));
        subjectsPanel.add(new JLabel("History: " + s.getSubject5() + " (" + s.getGrade5() + ")"));
        subjectsPanel.add(new JLabel("PI/PM: " + s.getSubject6() + " (" + s.getGrade6() + ")"));

        add(subjectsPanel, BorderLayout.CENTER);

        // Close Button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}