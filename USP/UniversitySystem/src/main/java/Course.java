import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String name;
    private int credits;
    private CourseType type;
    private List<Teacher> instructors;
    private List<Student> enrolledStudents;
    private List<Lesson> lessons;

    // Constructor
    public Course(String id, String name, int credits) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.instructors = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String courseId) {
        this.id = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public List<Teacher> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Teacher> instructors) {
        this.instructors = instructors;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    // JDBC Methods
    public void addStudent(Student student) {
        // Validate course availability and add student if possible
        if (!isFull()) {
            String insertQuery = "INSERT INTO course_registrations (student_id, course_id) VALUES (?, ?)";

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {

                pstmt.setString(1, student.getId());
                pstmt.setString(2, this.id);
                pstmt.executeUpdate();

                System.out.println("Student " + student.getFirstName() + " " + student.getLastName() + " enrolled in " + name);

            } catch (SQLException e) {
                System.out.println("Error adding student to the course.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Course " + name + " is full, cannot add student.");
        }
    }

    public void removeStudent(Student student) {
        // Remove student from the course
        String removeQuery = "DELETE FROM course_registrations WHERE student_id = ? AND course_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(removeQuery)) {

            pstmt.setString(1, student.getId());
            pstmt.setString(2, this.id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student " + student.getFirstName() + " " + student.getLastName() + " removed from " + name);
            } else {
                System.out.println("Student not enrolled in the course.");
            }

        } catch (SQLException e) {
            System.out.println("Error removing student from the course.");
            e.printStackTrace();
        }
    }

    public boolean isFull() {
        String checkAvailabilityQuery = "SELECT available_slots FROM courses WHERE course_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(checkAvailabilityQuery)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int availableSlots = rs.getInt("available_slots");
                return availableSlots <= 0;
            }

        } catch (SQLException e) {
            System.out.println("Error checking course availability.");
            e.printStackTrace();
        }

        return true; // Default to full if an error occurs
    }

    // Update available slots after a student registers
    public void updateAvailableSlots() {
        String updateSlotsQuery = "UPDATE courses SET available_slots = available_slots - 1 WHERE course_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(updateSlotsQuery)) {

            pstmt.setString(1, this.id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating available slots.");
            e.printStackTrace();
        }
    }
}
