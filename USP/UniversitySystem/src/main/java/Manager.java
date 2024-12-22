import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private String id;
    private String name;
    private String email;
    private String department;

    public Manager(String id, String name, String email, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public void approveRegistration(Student student, Course course) {
        String query = "INSERT INTO course_registrations (student_id, course_id) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, student.getId());
            pstmt.setString(2, course.getId());
            pstmt.executeUpdate();
            System.out.println("Registration approved for student " + student.getFirstName() + " in course " + course.getName());

        } catch (SQLException e) {
            System.out.println("Error approving registration.");
            e.printStackTrace();
        }
    }

    public void assignCourse(Teacher teacher, Course course) {
        String query = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, teacher.getId());
            pstmt.setString(2, course.getId());
            pstmt.executeUpdate();
            System.out.println("Course " + course.getName() + " assigned to teacher " + teacher.getName());

        } catch (SQLException e) {
            System.out.println("Error assigning course.");
            e.printStackTrace();
        }
    }

    public void manageNews(News news) {
        String query = "INSERT INTO news (content) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, news.getContent());
            pstmt.executeUpdate();
            System.out.println("News article published: " + news.getContent());

        } catch (SQLException e) {
            System.out.println("Error managing news.");
            e.printStackTrace();
        }
    }









    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartment(String department) {
        this.department = department;
    }




}
