import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Connection conn;

    public StudentDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertStudent(StudentModel student) {
        String sql = "INSERT INTO student (ic, name, teacherid, level, " +
                "subject1, grade1, subject2, grade2, subject3, grade3, " +
                "subject4, grade4, subject5, grade5, subject6, grade6, total, rank) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            int total = student.getSubject1() + student.getSubject2() + student.getSubject3()
                    + student.getSubject4() + student.getSubject5() + student.getSubject6();

            ps.setString(1, student.getIc());
            ps.setString(2, student.getName());
            ps.setInt(3, student.getTeacherId());
            ps.setString(4, student.getLevel());

            ps.setInt(5, student.getSubject1());
            ps.setString(6, student.getGrade1());
            ps.setInt(7, student.getSubject2());
            ps.setString(8, student.getGrade2());
            ps.setInt(9, student.getSubject3());
            ps.setString(10, student.getGrade3());
            ps.setInt(11, student.getSubject4());
            ps.setString(12, student.getGrade4());
            ps.setInt(13, student.getSubject5());
            ps.setString(14, student.getGrade5());
            ps.setInt(15, student.getSubject6());
            ps.setString(16, student.getGrade6());

            ps.setInt(17, total);
            ps.setInt(18, 0); // rank temporary

            int result = ps.executeUpdate();

            // update rank after insert
            updateRanks(student.getLevel(), student.getTeacherId());

            return result == 1;

        } catch (Exception e) {
            System.out.println("Insert ERROR: " + e.getMessage());
            return false;
        }
    }

    // =====================================
    // GET STUDENTS BY TEACHER
    // =====================================
    public List<StudentModel> getStudentsByTeacher(int teacherId) {
        List<StudentModel> list = new ArrayList<>();

        String sql = "SELECT * FROM student WHERE teacherid = ? ORDER BY level, rank ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudentModel s = new StudentModel(
                        rs.getInt("id"),
                        rs.getString("ic"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getInt("teacherid"),

                        rs.getInt("subject1"),
                        rs.getString("grade1"),
                        rs.getInt("subject2"),
                        rs.getString("grade2"),
                        rs.getInt("subject3"),
                        rs.getString("grade3"),
                        rs.getInt("subject4"),
                        rs.getString("grade4"),
                        rs.getInt("subject5"),
                        rs.getString("grade5"),
                        rs.getInt("subject6"),
                        rs.getString("grade6"),

                        rs.getInt("total"),
                        rs.getInt("rank")
                );

                list.add(s);
            }

        } catch (Exception e) {
            System.out.println("List ERROR: " + e.getMessage());
        }

        return list;
    }

    // =====================================
    // GET STUDENTS by Teacher + LEVEL (filter)
    // =====================================
    public List<StudentModel> getStudentsByTeacherAndLevel(int teacherId, String level) {
        List<StudentModel> list = new ArrayList<>();

        String sql = "SELECT * FROM student WHERE teacherid = ? AND level = ? ORDER BY rank ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ps.setString(2, level);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudentModel s = new StudentModel(
                        rs.getInt("id"),
                        rs.getString("ic"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getInt("teacherid"),

                        rs.getInt("subject1"),
                        rs.getString("grade1"),
                        rs.getInt("subject2"),
                        rs.getString("grade2"),
                        rs.getInt("subject3"),
                        rs.getString("grade3"),
                        rs.getInt("subject4"),
                        rs.getString("grade4"),
                        rs.getInt("subject5"),
                        rs.getString("grade5"),
                        rs.getInt("subject6"),
                        rs.getString("grade6"),

                        rs.getInt("total"),
                        rs.getInt("rank")
                );

                list.add(s);
            }

        } catch (Exception e) {
            System.out.println("Filter ERROR: " + e.getMessage());
        }

        return list;
    }

    // 👇 CLOSE the updateRanks method properly HERE
    public void updateRanks(String level,int teacherId) {

        String sqlReset = "UPDATE student SET rank = 0 WHERE level = ? AND teacherid = ?";
        String sqlSelect = "SELECT id FROM student WHERE level = ? AND teacherid = ? ORDER BY total DESC";
        String sqlUpdateRank = "UPDATE student SET rank = ? WHERE id = ?";

        try {

            PreparedStatement ps1 = conn.prepareStatement(sqlReset);
            ps1.setString(1, level);
            ps1.setInt(2, teacherId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sqlSelect);
            ps2.setString(1, level);
            ps2.setInt(2, teacherId);
            ResultSet rs = ps2.executeQuery();

            int rank = 1;

            while (rs.next()) {
                int studentId = rs.getInt("id");

                PreparedStatement ps3 = conn.prepareStatement(sqlUpdateRank);
                ps3.setInt(1, rank);
                ps3.setInt(2, studentId);
                ps3.executeUpdate();

                rank++;
            }

        } catch (Exception e) {
            System.out.println("Rank ERROR: " + e.getMessage());
        }
    }

    private int getTeacherIdFromConnection() {
        return SessionManager.getLoggedInTeacherId();
    }

    public static boolean addStudent(StudentModel student) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StudentDAO dao = new StudentDAO(conn);
            return dao.insertStudent(student);
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    public static List<StudentModel> getAllStudents() {
        List<StudentModel> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM student ORDER BY level, rank ASC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudentModel s = new StudentModel(
                        rs.getInt("id"),
                        rs.getString("ic"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getInt("teacherid"),
                        rs.getInt("subject1"), rs.getString("grade1"),
                        rs.getInt("subject2"), rs.getString("grade2"),
                        rs.getInt("subject3"), rs.getString("grade3"),
                        rs.getInt("subject4"), rs.getString("grade4"),
                        rs.getInt("subject5"), rs.getString("grade5"),
                        rs.getInt("subject6"), rs.getString("grade6"),
                        rs.getInt("total"),
                        rs.getInt("rank")
                );
                list.add(s);
            }
        } catch (Exception e) {
            System.out.println("Error getting all students: " + e.getMessage());
        }
        return list;
    }

    public static StudentModel getStudentById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM student WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new StudentModel(
                        rs.getInt("id"),
                        rs.getString("ic"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getInt("teacherid"),
                        rs.getInt("subject1"), rs.getString("grade1"),
                        rs.getInt("subject2"), rs.getString("grade2"),
                        rs.getInt("subject3"), rs.getString("grade3"),
                        rs.getInt("subject4"), rs.getString("grade4"),
                        rs.getInt("subject5"), rs.getString("grade5"),
                        rs.getInt("subject6"), rs.getString("grade6"),
                        rs.getInt("total"),
                        rs.getInt("rank")
                );
            }
        } catch (Exception e) {
            System.out.println("Error getting student by ID: " + e.getMessage());
        }
        return null;
    }

    public static int getStudentRank(String ic) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT rank FROM student WHERE ic = ?");
            ps.setString(1, ic);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("rank");
            }
        } catch (Exception e) {
            System.out.println("Error getting rank: " + e.getMessage());
        }
        return 0;
    }
    public static List<StudentModel> getStudentsByTeacherStatic(int teacherId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StudentDAO dao = new StudentDAO(conn);
            return dao.getStudentsByTeacher(teacherId);
        } catch (Exception e) {
            System.out.println("Error getting students by teacher: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public static List<StudentModel> getStudentsByTeacherAndLevelStatic(int teacherId, String level) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StudentDAO dao = new StudentDAO(conn);
            return dao.getStudentsByTeacherAndLevel(teacherId, level);
        } catch (Exception e) {
            System.out.println("Error getting students by teacher and level: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    // =====================================
// UPDATE STUDENT
// =====================================
    public static boolean updateStudent(StudentModel student) {
        String sql = "UPDATE student SET ic=?, name=?, level=?, " +
                "subject1=?, grade1=?, subject2=?, grade2=?, subject3=?, grade3=?, " +
                "subject4=?, grade4=?, subject5=?, grade5=?, subject6=?, grade6=?, " +
                "total=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            int total = student.getSubject1() + student.getSubject2() + student.getSubject3() +
                    student.getSubject4() + student.getSubject5() + student.getSubject6();

            ps.setString(1, student.getIc());
            ps.setString(2, student.getName());
            ps.setString(3, student.getLevel());
            ps.setInt(4, student.getSubject1());
            ps.setString(5, student.getGrade1());
            ps.setInt(6, student.getSubject2());
            ps.setString(7, student.getGrade2());
            ps.setInt(8, student.getSubject3());
            ps.setString(9, student.getGrade3());
            ps.setInt(10, student.getSubject4());
            ps.setString(11, student.getGrade4());
            ps.setInt(12, student.getSubject5());
            ps.setString(13, student.getGrade5());
            ps.setInt(14, student.getSubject6());
            ps.setString(15, student.getGrade6());
            ps.setInt(16, total);
            ps.setInt(17, student.getId());

            int result = ps.executeUpdate();

            // Update ranks after editing
            if (result > 0) {
                StudentDAO dao = new StudentDAO(conn);
                dao.updateRanks(student.getLevel(), student.getTeacherId());
            }

            return result > 0;

        } catch (Exception e) {
            System.out.println("Update ERROR: " + e.getMessage());
            return false;
        }
    }

    // =====================================
// DELETE STUDENT
// =====================================
    public static boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM student WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get student info before deleting (for rank update)
            StudentModel student = getStudentById(studentId);
            if (student == null) return false;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);

            int result = ps.executeUpdate();

            // Update ranks after deletion
            if (result > 0) {
                StudentDAO dao = new StudentDAO(conn);
                dao.updateRanks(student.getLevel(), student.getTeacherId());
            }

            return result > 0;

        } catch (Exception e) {
            System.out.println("Delete ERROR: " + e.getMessage());
            return false;
        }
    }

}