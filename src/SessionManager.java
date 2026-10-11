public class SessionManager {
    private static int loggedInTeacherId = -1;
    private static String loggedInTeacherName = "";

    public static void setLoggedInTeacher(int teacherId, String teacherName) {
        loggedInTeacherId = teacherId;
        loggedInTeacherName = teacherName;
    }

    public static int getLoggedInTeacherId() {
        return loggedInTeacherId;
    }

    public static String getLoggedInTeacherName() {
        return loggedInTeacherName;
    }

    public static boolean isLoggedIn() {
        return loggedInTeacherId != -1;
    }

    public static void logout() {
        loggedInTeacherId = -1;
        loggedInTeacherName = "";
    }
}
