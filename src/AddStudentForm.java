import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddStudentForm extends JFrame {

    private JTextField tfIC, tfName;
    private JTextField tfBM, tfBI, tfMath, tfScience, tfHistory, tfPIPM;
    private JComboBox<String> levelBox, cbBMGrade, cbBIGrade, cbMathGrade, cbScienceGrade, cbHistoryGrade, cbPIPMGrade;

    private String[] grades = {"A", "B", "C", "D", "E", "F"};

    public AddStudentForm() {
        setTitle("Add Student Performance");
        setSize(450, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblIC = new JLabel("IC Number:");
        lblIC.setBounds(30, 20, 100, 25);
        add(lblIC);

        tfIC = new JTextField();
        tfIC.setBounds(150, 20, 200, 25);
        add(tfIC);

        JLabel lblName = new JLabel("Student Name:");
        lblName.setBounds(30, 60, 100, 25);
        add(lblName);

        tfName = new JTextField();
        tfName.setBounds(150, 60, 200, 25);
        add(tfName);

        JLabel lblLevel = new JLabel("Form/Standard:");
        lblLevel.setBounds(30, 100, 100, 25);
        add(lblLevel);

        String[] levels = {"1", "2", "3", "4", "5", "6"};
        levelBox = new JComboBox<>(levels);
        levelBox.setBounds(150, 100, 150, 25);
        add(levelBox);


        // Subject labels + marks + grade dropdown

        addSubject("BM :", 140, tfBM = new JTextField(), cbBMGrade = new JComboBox<>(grades));
        addSubject("BI :", 180, tfBI = new JTextField(), cbBIGrade = new JComboBox<>(grades));
        addSubject("Math :", 220, tfMath = new JTextField(), cbMathGrade = new JComboBox<>(grades));
        addSubject("Science :", 260, tfScience = new JTextField(), cbScienceGrade = new JComboBox<>(grades));
        addSubject("History :", 300, tfHistory = new JTextField(), cbHistoryGrade = new JComboBox<>(grades));
        addSubject("PI/PM :", 340, tfPIPM = new JTextField(), cbPIPMGrade = new JComboBox<>(grades));

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(150, 400, 100, 35);
        add(btnSave);



        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStudent();
            }
        });

        setLocationRelativeTo(null);
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

    private void saveStudent() {
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

            StudentModel student = new StudentModel(
                    ic, name, level,
                    bm, gBM,
                    bi, gBI,
                    math, gMath,
                    sci, gSci,
                    his, gHis,
                    pipm, gPIPM);

            student.setTeacherId(SessionManager.getLoggedInTeacherId());
            student.setLevel(level);
            student.setTotal(total);

            boolean inserted = StudentDAO.addStudent(student);

            if (inserted) {
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding student!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
