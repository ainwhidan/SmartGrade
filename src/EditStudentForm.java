import javax.swing.*;
import java.awt.*;

public class EditStudentForm extends JFrame {
    private JTextField tfIC, tfName;
    private JTextField tfBM, tfBI, tfMath, tfScience, tfHistory, tfPIPM;
    private JComboBox<String> levelBox, cbBMGrade, cbBIGrade, cbMathGrade, cbScienceGrade, cbHistoryGrade, cbPIPMGrade;
    private String[] grades = {"A", "B", "C", "D", "E", "F"};
    private StudentModel student;

    public EditStudentForm(int studentId) {
        // Load student data
        student = StudentDAO.getStudentById(studentId);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found!");
            dispose();
            return;
        }

        setTitle("Edit Student Details");
        setSize(450, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblIC = new JLabel("IC Number:");
        lblIC.setBounds(30, 20, 100, 25);
        add(lblIC);
        tfIC = new JTextField(student.getIc());
        tfIC.setBounds(150, 20, 200, 25);
        add(tfIC);

        JLabel lblName = new JLabel("Student Name:");
        lblName.setBounds(30, 60, 100, 25);
        add(lblName);
        tfName = new JTextField(student.getName());
        tfName.setBounds(150, 60, 200, 25);
        add(tfName);

        JLabel lblLevel = new JLabel("Form/Standard:");
        lblLevel.setBounds(30, 100, 100, 25);
        add(lblLevel);
        String[] levels = {"1", "2", "3", "4", "5", "6"};
        levelBox = new JComboBox<>(levels);
        levelBox.setSelectedItem(student.getLevel());
        levelBox.setBounds(150, 100, 150, 25);
        add(levelBox);

        // Subject fields with existing data
        addSubject("BM :", 140, tfBM = new JTextField(String.valueOf(student.getSubject1())),
                cbBMGrade = new JComboBox<>(grades));
        cbBMGrade.setSelectedItem(student.getGrade1());

        addSubject("BI :", 180, tfBI = new JTextField(String.valueOf(student.getSubject2())),
                cbBIGrade = new JComboBox<>(grades));
        cbBIGrade.setSelectedItem(student.getGrade2());

        addSubject("Math :", 220, tfMath = new JTextField(String.valueOf(student.getSubject3())),
                cbMathGrade = new JComboBox<>(grades));
        cbMathGrade.setSelectedItem(student.getGrade3());

        addSubject("Science :", 260, tfScience = new JTextField(String.valueOf(student.getSubject4())),
                cbScienceGrade = new JComboBox<>(grades));
        cbScienceGrade.setSelectedItem(student.getGrade4());

        addSubject("History :", 300, tfHistory = new JTextField(String.valueOf(student.getSubject5())),
                cbHistoryGrade = new JComboBox<>(grades));
        cbHistoryGrade.setSelectedItem(student.getGrade5());

        addSubject("PI/PM :", 340, tfPIPM = new JTextField(String.valueOf(student.getSubject6())),
                cbPIPMGrade = new JComboBox<>(grades));
        cbPIPMGrade.setSelectedItem(student.getGrade6());

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 400, 100, 35);
        add(btnUpdate);
        btnUpdate.addActionListener(e -> updateStudent());

        setVisible(true);
    }

    private void addSubject(String label, int y, JTextField tf, JComboBox<String> cb) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(30, y, 100, 25);
        add(lbl);
        tf.setBounds(150, y, 50, 25);
        add(tf);
        cb.setBounds(220, y, 70, 25);
        add(cb);
    }

    private void updateStudent() {
        try {
            String ic = tfIC.getText();
            String name = tfName.getText();
            String level = levelBox.getSelectedItem().toString();

            int bm = Integer.parseInt(tfBM.getText());
            int bi = Integer.parseInt(tfBI.getText());
            int math = Integer.parseInt(tfMath.getText());
            int sci = Integer.parseInt(tfScience.getText());
            int his = Integer.parseInt(tfHistory.getText());
            int pipm = Integer.parseInt(tfPIPM.getText());

            String gBM = cbBMGrade.getSelectedItem().toString();
            String gBI = cbBIGrade.getSelectedItem().toString();
            String gMath = cbMathGrade.getSelectedItem().toString();
            String gSci = cbScienceGrade.getSelectedItem().toString();
            String gHis = cbHistoryGrade.getSelectedItem().toString();
            String gPIPM = cbPIPMGrade.getSelectedItem().toString();

            int total = bm + bi + math + sci + his + pipm;

            // Update student object
            student.setIc(ic);
            student.setName(name);
            student.setLevel(level);
            student.setBm(bm);
            student.setGBM(gBM);
            student.setBi(bi);
            student.setGBI(gBI);
            student.setMath(math);
            student.setGMath(gMath);
            student.setSci(sci);
            student.setGSci(gSci);
            student.setHis(his);
            student.setGHis(gHis);
            student.setPipm(pipm);
            student.setGPipm(gPIPM);
            student.setTotal(total);

            boolean updated = StudentDAO.updateStudent(student);

            if (updated) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating student!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
