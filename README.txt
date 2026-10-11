================================================================================
STUDENT PERFORMANCE TRACKER - SETUP GUIDE
================================================================================

GROUP: 10
SUBJECT: DEP3033 (C)
COURSE: BACHELOR OF SOFTWARE ENGINEERING (SOFTWARE EDUCATION) - AC10

================================================================================
1. REQUIREMENTS
================================================================================
- Java JDK 17 or higher
- XAMPP (for MySQL database)
- IntelliJ IDEA (recommended) or any Java IDE

================================================================================
2. DATABASE SETUP
================================================================================
Step 1: Install and Start XAMPP
   - Download XAMPP from: https://www.apachefriends.org
   - Install XAMPP
   - Open XAMPP Control Panel
   - Click "Start" for MySQL

Step 2: Import Database
   - Open browser: http://localhost/phpmyadmin
   - Click "New" to create database
   - Database name: student_performance (or your database name)
   - Click "Create"
   - Select the database
   - Click "Import" tab
   - Click "Choose File"
   - Navigate to: database/[your_sql_file].sql
   - Click "Import"
   - Wait for success message

================================================================================
3. OPEN PROJECT IN INTELLIJ
================================================================================
   - File → Open
   - Select StudentPerformanceTracker folder
   - Click OK
   - Wait for indexing to complete

================================================================================
4. ADD MYSQL LIBRARY
================================================================================
   - File → Project Structure (Ctrl+Alt+Shift+S)
   - Libraries → Click "+" → Java
   - Navigate to: lib/mysql-connector-j-9.4.0.jar
   - Click OK → Apply → OK

================================================================================
5. CONFIGURE DATABASE CONNECTION
================================================================================
Open: src/DatabaseConnection.java

Check these settings:
   URL: jdbc:mysql://localhost:3306/student_performance
   Username: root
   Password: (leave empty)

================================================================================
6. RUN THE APPLICATION
================================================================================
   - Find WelcomePage.java or MainMenu.java (your main class)
   - Right-click → Run
   - The application should start

================================================================================
7. TROUBLESHOOTING
================================================================================
- If "No suitable driver": Add mysql-connector JAR to libraries
- If "Access denied": Check XAMPP MySQL is running
- If "Unknown database": Import SQL file again

================================================================================
CONTACT: d20231106418@siswa.upsi.edu.my
================================================================================
`