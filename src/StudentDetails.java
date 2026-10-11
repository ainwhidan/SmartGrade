import javax.swing.*;
import java.awt.*;

public class StudentDetails extends JFrame {

    private int studentId;
    private JLabel lblName, lblIC, lblForm, lblGrade;

    public StudentDetails(int studentId) {
        this.studentId = studentId;
        setTitle("Student Details");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        lblName = new JLabel("Name: ");
        lblIC = new JLabel("IC Number: ");
        lblForm = new JLabel("Form: ");
        lblGrade = new JLabel("Grade: ");

        add(lblName);
        add(new JLabel());
        add(lblIC);
        add(new JLabel());
        add(lblForm);
        add(new JLabel());
        add(lblGrade);
        add(new JLabel());

        loadStudentDetails();

        setVisible(true);
    }

    private void loadStudentDetails() {
        StudentModel student = StudentDAO.getStudentById(studentId);

        if (student != null) {
            lblName.setText("Name: " + student.getName());
            lblIC.setText("IC: " + student.getIc());
            lblForm.setText("Form/Standard: " + student.getLevel());

        } else {
            JOptionPane.showMessageDialog(this, "Student Not Found!");
        }
    }
}
