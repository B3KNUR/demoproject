import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Librarian {
    private String id;
    private String name;

    // Constructor
    public Librarian() {}

    public Librarian(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Add a book to the library (database)
    public void addBook(Book book) {
        try (Connection connection = DBConnection.getConnection()) {
            book.saveToDatabase();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    // Remove a book from the library (database)
    public void removeBook(String code) {
        try (Connection connection = DBConnection.getConnection()) {
            Book book = Book.fetchFromDatabase(code);
            if (book != null) {
                book.deleteFromDatabase();
                System.out.println("Book removed successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing book: " + e.getMessage());
        }
    }

    // Borrow a book
    public void borrowBook(String code, String studentId, LocalDate dueDate) {
        try (Connection connection = DBConnection.getConnection()) {
            Book book = Book.fetchFromDatabase(code);
            if (book != null && book.getIsAvailable()) {
                String sql = "INSERT INTO BorrowedBooks (book_code, student_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, code);
                    stmt.setString(2, studentId);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.setDate(4, Date.valueOf(dueDate));
                    stmt.executeUpdate();

                    book.markAsBorrowed();
                    book.updateAvailabilityInDatabase();

                    System.out.println("Book borrowed successfully.");
                }
            } else {
                System.out.println("Book is not available.");
            }
        } catch (SQLException e) {
            System.err.println("Error borrowing book: " + e.getMessage());
        }
    }

    // Return a book
    public void returnBook(String code, String studentId) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "DELETE FROM BorrowedBooks WHERE book_code = ? AND student_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, code);
                stmt.setString(2, studentId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    Book book = Book.fetchFromDatabase(code);
                    if (book != null) {
                        book.markAsReturned();
                        book.updateAvailabilityInDatabase();
                    }
                    System.out.println("Book returned successfully.");
                } else {
                    System.out.println("No record of this book being borrowed by the student.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
    }

    // List all books in the library
    public List<Book> listBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Books";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String code = rs.getString("code");
                    boolean isAvailable = rs.getBoolean("is_available");

                    Book book = new Book(title, author, code);
                    book.setAvailable(isAvailable);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error listing books: " + e.getMessage());
        }
        return books;
    }

    // Get borrowing history of a specific student
    public List<String> getBorrowingHistory(String studentId) {
        List<String> history = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT book_code, borrow_date, due_date FROM BorrowedBooks WHERE student_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String bookCode = rs.getString("book_code");
                        LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
                        LocalDate dueDate = rs.getDate("due_date").toLocalDate();

                        history.add(String.format("Book Code: %s, Borrow Date: %s, Due Date: %s", bookCode, borrowDate, dueDate));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowing history: " + e.getMessage());
        }
        return history;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}