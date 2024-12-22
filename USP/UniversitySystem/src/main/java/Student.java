import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Student implements Researcher {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private int year;
    private double gpa;
    private ResearchProject diplomaProject;
    private List<ResearchProject> researchProjects;
    private List<ResearchPaper> researchPapers;
    private List<Book> borrowedBooks;

    public Student(){};

    public Student(String id, String firstName, String lastName, String email, String major, int year, double gpa) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.year = year;
        this.gpa = gpa;
        this.researchProjects = new ArrayList<>();
        this.researchPapers = new ArrayList<>();
    }

    @Override
    public int calculateHIndex() {
        String query = "SELECT citations FROM research_papers WHERE student_id = ?";
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

    public List<ResearchPaper> printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> papers = new ArrayList<>();
        String query = "SELECT * FROM research_papers WHERE student_id = ?";

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


    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    public double getGpa() {
        return gpa;
    }

    public ResearchProject getDiplomaProject() {
        return diplomaProject;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public void setDiplomaProject(ResearchProject diplomaProject) {
        this.diplomaProject = diplomaProject;
    }

    public void borrowBook(User user, String code) {
        String checkAvailabilityQuery = "SELECT * FROM books WHERE code = ?";
        String borrowBookQuery = "INSERT INTO borrowed_books (book_id, user_id) VALUES (?, ?)";
        String updateBookAvailabilityQuery = "UPDATE books SET available = FALSE WHERE code = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkAvailabilityQuery)) {

            checkStmt.setString(1, code);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean isAvailable = rs.getBoolean("available");
                int bookId = rs.getInt("id");

                if (isAvailable) {
                    try (PreparedStatement borrowStmt = connection.prepareStatement(borrowBookQuery);
                         PreparedStatement updateStmt = connection.prepareStatement(updateBookAvailabilityQuery)) {

                        connection.setAutoCommit(false);

                        borrowStmt.setInt(1, bookId);
                        borrowStmt.setString(2, user.getId());
                        borrowStmt.executeUpdate();

                        updateStmt.setString(1, code);
                        updateStmt.executeUpdate();

                        connection.commit();
                        System.out.println("Book borrowed successfully.");
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("Error borrowing book. Transaction rolled back.");
                        e.printStackTrace();
                    } finally {
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Book is currently not available.");
                }
            } else {
                System.out.println("Book with code " + code + " not found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to borrow book.");
            e.printStackTrace();
        }
    }

    public void returnBook(User user, String code) {
        String findBorrowedBookQuery = "SELECT bb.id FROM borrowed_books bb " +
                "JOIN books b ON bb.book_id = b.id " +
                "WHERE b.code = ? AND bb.user_id = ? AND bb.return_date IS NULL";
        String updateBorrowedBookQuery = "UPDATE borrowed_books SET return_date = CURRENT_TIMESTAMP WHERE id = ?";
        String updateBookAvailabilityQuery = "UPDATE books SET available = TRUE WHERE code = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findBorrowedBookQuery)) {

            findStmt.setString(1, code);
            findStmt.setString(2, user.getId());
            ResultSet rs = findStmt.executeQuery();

            if (rs.next()) {
                int borrowedBookId = rs.getInt("id");

                try (PreparedStatement updateBorrowStmt = connection.prepareStatement(updateBorrowedBookQuery);
                     PreparedStatement updateBookStmt = connection.prepareStatement(updateBookAvailabilityQuery)) {

                    connection.setAutoCommit(false);

                    updateBorrowStmt.setInt(1, borrowedBookId);
                    updateBorrowStmt.executeUpdate();

                    updateBookStmt.setString(1, code);
                    updateBookStmt.executeUpdate();

                    connection.commit();
                    System.out.println("Book returned successfully.");
                } catch (Exception e) {
                    connection.rollback();
                    System.out.println("Error returning book. Transaction rolled back.");
                    e.printStackTrace();
                } finally {
                    connection.setAutoCommit(true);
                }
            } else {
                System.out.println("No borrowed record found for code " + code + ".");
            }

        } catch (Exception e) {
            System.out.println("Failed to return book.");
            e.printStackTrace();
        }
    }

    public List<Book> getBorrowedBooks(User user) {
        List<Book> borrowedBooks = new ArrayList<>();
        String query = "SELECT b.title, b.author, b.code, bb.borrow_date FROM borrowed_books bb " +
                "JOIN books b ON bb.book_id = b.id " +
                "WHERE bb.user_id = ? AND bb.return_date IS NULL";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book borrowedBook = new Book(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("author")
                );
                borrowedBooks.add(borrowedBook);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve borrowed books.");
            e.printStackTrace();
        }

        return borrowedBooks;
    }

    public void rateTeacher(Teacher teacher, int rating) {

        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        String insertRatingQuery = "INSERT INTO teacher_ratings (teacher_id, student_id, rating) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertRatingQuery)) {

            pstmt.setString(1, teacher.getId());
            pstmt.setString(2, this.id);
            pstmt.setInt(3, rating);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rating submitted successfully.");
            } else {
                System.out.println("Failed to submit rating.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred while submitting the rating.");
            e.printStackTrace();
        }
    }

    public void registerForCourse(Course course) {
        // Validate course availability
        String checkAvailabilityQuery = "SELECT available_slots FROM courses WHERE course_id = ?";
        String registerForCourseQuery = "INSERT INTO course_registrations (student_id, course_id) VALUES (?, ?)";
        String updateCourseSlotsQuery = "UPDATE courses SET available_slots = available_slots - 1 WHERE course_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkAvailabilityQuery)) {

            checkStmt.setString(1, course.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int availableSlots = rs.getInt("available_slots");

                if (availableSlots > 0) {
                    // Register the student for the course
                    try (PreparedStatement registerStmt = connection.prepareStatement(registerForCourseQuery);
                         PreparedStatement updateSlotsStmt = connection.prepareStatement(updateCourseSlotsQuery)) {

                        connection.setAutoCommit(false);

                        registerStmt.setString(1, this.id);
                        registerStmt.setString(2, course.getId());
                        registerStmt.executeUpdate();

                        updateSlotsStmt.setString(1, course.getId());
                        updateSlotsStmt.executeUpdate();

                        connection.commit();
                        System.out.println("Successfully registered for the course.");
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("Error during registration. Transaction rolled back.");
                        e.printStackTrace();
                    } finally {
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("No slots available for the course.");
                }
            } else {
                System.out.println("Course not found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to register for the course.");
            e.printStackTrace();
        }
    }








}
