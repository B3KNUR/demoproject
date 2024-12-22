import java.sql.*;

public class Book {
    private String title;
    private String author;
    private String code;
    private boolean isAvailable;

    // Constructor
    public Book(String title, String author, String code) {
        this.title = title;
        this.author = author;
        this.code = code;
        this.isAvailable = true; // Default to available when created
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCode() {
        return code;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    // Setters
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Mark as borrowed
    public void markAsBorrowed() {
        isAvailable = false;
    }

    // Mark as returned
    public void markAsReturned() {
        isAvailable = true;
    }

    // JDBC Methods

    // Save book to the database
    public void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO Books (title, author, code, is_available) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, code);
            stmt.setBoolean(4, isAvailable);
            stmt.executeUpdate();
        }
    }

    // Fetch book from the database
    public static Book fetchFromDatabase(String code) throws SQLException {
        String sql = "SELECT * FROM Books WHERE code = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String bookCode = rs.getString("code");
                    boolean isAvailable = rs.getBoolean("is_available");
                    Book book = new Book(title, author, bookCode);
                    book.setAvailable(isAvailable);
                    return book;
                }
            }
        }
        return null;
    }

    // Update availability in the database
    public void updateAvailabilityInDatabase() throws SQLException {
        String sql = "UPDATE Books SET is_available = ? WHERE code = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isAvailable);
            stmt.setString(2, code);
            stmt.executeUpdate();
        }
    }

    // Delete book from the database
    public void deleteFromDatabase() throws SQLException {
        String sql = "DELETE FROM Books WHERE code = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.executeUpdate();
        }
    }
}