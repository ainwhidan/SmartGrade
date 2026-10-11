import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewStudents extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private String level;

    public ViewStudents(String level) {
        this.level = level;

        setTitle("View Students - Form/Standard " + level);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("Students in Form/Standard " + level, SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        // Create table model (removed Total and Level columns)
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        model.setColumnIdentifiers(new Object[]{"ID", "IC Number", "Name", "Rank"});

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Hide ID column
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load students
        loadStudents();

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewDetailsBtn = new JButton("View Details");
        JButton editBtn = new JButton("Edit Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton exportBtn = new JButton("Export PDF");
        JButton closeBtn = new JButton("Close");

        viewDetailsBtn.addActionListener(e -> viewSelectedStudent());
        editBtn.addActionListener(e -> editSelectedStudent());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        exportBtn.addActionListener(e -> exportToPDF());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadStudents() {
        model.setRowCount(0); // Clear existing rows

        // Get logged-in teacher's ID
        int teacherId = SessionManager.getLoggedInTeacherId();

        // Get students for this teacher and level only
        List<StudentModel> students = StudentDAO.getStudentsByTeacherAndLevelStatic(teacherId, level);

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No students found in Form/Standard " + level,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        for (StudentModel s : students) {
            model.addRow(new Object[]{
                    s.getId(),           // Hidden column
                    s.getIc(),
                    s.getName(),
                    s.getRank()
            });
        }
    }

    private void viewSelectedStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student first!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get student ID from hidden column
        int studentId = (int) model.getValueAt(selectedRow, 0);
        new StudentDetailsForm(studentId);
    }
    private void editSelectedStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student first!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) model.getValueAt(selectedRow, 0);
        new EditStudentForm(studentId);

        // Refresh the table after editing
        dispose();
        new ViewStudents(level);
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student first!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentName = (String) model.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + studentName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int studentId = (int) model.getValueAt(selectedRow, 0);
            boolean deleted = StudentDAO.deleteStudent(studentId);

            if (deleted) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudents(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToPDF() {
        try {
            // Create simple text report
            StringBuilder report = new StringBuilder();
            report.append("====================================\n");
            report.append("STUDENT PERFORMANCE REPORT\n");
            report.append("Form/Standard: ").append(level).append("\n");
            report.append("Teacher: ").append(SessionManager.getLoggedInTeacherName()).append("\n");
            report.append("====================================\n\n");

            int teacherId = SessionManager.getLoggedInTeacherId();
            List<StudentModel> students = StudentDAO.getStudentsByTeacherAndLevelStatic(teacherId, level);

            for (StudentModel s : students) {
                report.append("Rank: ").append(s.getRank()).append("\n");
                report.append("Name: ").append(s.getName()).append("\n");
                report.append("IC: ").append(s.getIc()).append("\n");
                report.append("Total Marks: ").append(s.getTotal()).append("\n");
                report.append("Subjects:\n");
                report.append("  BM: ").append(s.getSubject1()).append(" (").append(s.getGrade1()).append(")\n");
                report.append("  BI: ").append(s.getSubject2()).append(" (").append(s.getGrade2()).append(")\n");
                report.append("  Math: ").append(s.getSubject3()).append(" (").append(s.getGrade3()).append(")\n");
                report.append("  Science: ").append(s.getSubject4()).append(" (").append(s.getGrade4()).append(")\n");
                report.append("  History: ").append(s.getSubject5()).append(" (").append(s.getGrade5()).append(")\n");
                report.append("  PI/PM: ").append(s.getSubject6()).append(" (").append(s.getGrade6()).append(")\n");
                report.append("------------------------------------\n");
            }

            // Show in a text area with print option
            JTextArea textArea = new JTextArea(report.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton printBtn = new JButton("Print");
            printBtn.addActionListener(e -> {
                try {
                    textArea.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
                }
            });

            JPanel btnPanel = new JPanel();
            btnPanel.add(printBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Export Report", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage());
        }
    }
}