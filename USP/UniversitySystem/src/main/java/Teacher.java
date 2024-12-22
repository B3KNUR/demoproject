import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Teacher implements Researcher {
    private String name;
    private String id;
    private String email;
    private String password;
    private String department;
    private List<Course> courses;
    private List<Complaint> complaints;
    private List<ResearchProject> researchProjects;
    private List<ResearchPaper> researchPapers;

    public Teacher(String name, String id, String email, String password, String department) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.department = department;
        this.courses = new ArrayList<>();
        this.complaints = new ArrayList<>();
        this.researchProjects = new ArrayList<>();
        this.researchPapers = new ArrayList<>();
    }

    @Override
    public int calculateHIndex() {
        String query = "SELECT citations FROM research_papers WHERE teacher_id = ?";
        int hIndex = 0;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int citations = rs.getInt("citations");
                if (citations > hIndex) {
                    hIndex++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hIndex;
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        String query = "INSERT INTO research_projects (topic) VALUES (?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, project.getTopic());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                int projectId = generatedKeys.getInt(1);
                // связываем проект с учителем
                String linkQuery = "INSERT INTO researchers_projects (researcher_id, project_id) VALUES (?, ?)";
                try (PreparedStatement linkPstmt = connection.prepareStatement(linkQuery)) {
                    linkPstmt.setString(1, this.id);
                    linkPstmt.setInt(2, projectId);
                    linkPstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        List<ResearchProject> projects = new ArrayList<>();
        String query = "SELECT rp.topic FROM research_projects rp JOIN researchers_projects rrp ON rp.id = rrp.project_id WHERE rrp.researcher_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ResearchProject project = new ResearchProject(rs.getString("topic"));
                projects.add(project);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    @Override
    public List<ResearchPaper> printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> papers = new ArrayList<>();
        String query = "SELECT * FROM research_papers WHERE teacher_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ResearchPaper paper = new ResearchPaper(
                        rs.getString("title"),
                        // преобразуйте авторов в List<String> из строки
                        new ArrayList<>(),
                        rs.getString("journal"),
                        rs.getInt("citations"),
                        rs.getString("pages"),
                        rs.getString("doi"),
                        rs.getDate("publicationDate")
                );
                papers.add(paper);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        papers.sort(c);
        return papers;
    }



    public void assignMark(Student student, int courseId, int mark) {
        String query = "INSERT INTO marks (student_id, course_id, mark) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, student.getId());
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, mark);
            pstmt.executeUpdate();
            System.out.println("Mark assigned successfully.");

        } catch (Exception e) {
            System.out.println("Failed to assign mark.");
            e.printStackTrace();
        }
    }

    public void sendComplaint(Student student, String urgencyLevel, String complaint) {
        String query = "INSERT INTO complaints (student_id, teacher_id, urgency_level, complaint) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, student.getId());
            pstmt.setString(2, this.id);
            pstmt.setString(3, urgencyLevel);
            pstmt.setString(4, complaint);
            pstmt.executeUpdate();
            System.out.println("Complaint sent successfully.");

        } catch (Exception e) {
            System.out.println("Failed to send complaint.");
            e.printStackTrace();
        }
    }

    public List<Course> viewCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE teacher_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve courses.");
            e.printStackTrace();
        }

        return courses;
    }

    public List<Student> viewStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.id, s.name FROM students s JOIN enrollments e ON s.id = e.student_id WHERE e.course_id IN (SELECT id FROM courses WHERE teacher_id = ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("major"),
                        rs.getInt("year"),
                        rs.getDouble("gpa")
                );
                students.add(student);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve students.");
            e.printStackTrace();
        }

        return students;
    }

    public void addCourse(Course course) {
        String query = "INSERT INTO courses (name, credits, teacher_id) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setString(3, this.id);
            pstmt.executeUpdate();
            System.out.println("Course added successfully.");

        } catch (Exception e) {
            System.out.println("Failed to add course.");
            e.printStackTrace();
        }
    }

    public void manageCourse(int courseId, String newName, int newCredits) {
        String query = "UPDATE courses SET name = ?, credits = ? WHERE id = ? AND teacher_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, newCredits);
            pstmt.setInt(3, courseId);
            pstmt.setString(4, this.id);
            pstmt.executeUpdate();
            System.out.println("Course updated successfully.");

        } catch (Exception e) {
            System.out.println("Failed to manage course.");
            e.printStackTrace();
        }
    }

    public List<String> viewRequests() {
        List<String> requests = new ArrayList<>();
        String query = "SELECT request FROM course_requests WHERE teacher_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(rs.getString("request"));
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve course requests.");
            e.printStackTrace();
        }

        return requests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    @Override
    public String toString() {
        return "Teacher{id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", department=" + department;
    }
}
