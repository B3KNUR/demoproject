import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class News {
    private String title;
    private String content;
    private boolean isPinned;
    private List<String> comments;

    // Constructor
    public News(String title, String content) {
        this.title = title;
        this.content = content;
        this.isPinned = false;
        this.comments = new ArrayList<>();
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public List<String> getComments() {
        return comments;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    // Methods
    public void addComment(String comment) {
        comments.add(comment);
    }

    public void pinNews() {
        this.isPinned = true;
    }

    // JDBC Methods
    public void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO News (title, content, isPinned) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setBoolean(3, isPinned);
            stmt.executeUpdate();
        }
    }

    public static News fetchFromDatabase(String title) throws SQLException {
        String sql = "SELECT * FROM News WHERE title = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    News news = new News(rs.getString("title"), rs.getString("content"));
                    news.setIsPinned(rs.getBoolean("isPinned"));
                    news.comments = fetchComments(title);
                    return news;
                }
            }
        }
        return null;
    }

    public static List<String> fetchComments(String newsTitle) throws SQLException {
        String sql = "SELECT comment FROM Comments WHERE news_title = ?";
        List<String> comments = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newsTitle);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(rs.getString("comment"));
                }
            }
        }
        return comments;
    }

    public void addCommentToDatabase(String comment) throws SQLException {
        String sql = "INSERT INTO Comments (news_title, comment) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, this.title);
            stmt.setString(2, comment);
            stmt.executeUpdate();
        }
        this.comments.add(comment);
    }
}


//CREATE TABLE News (
//        title VARCHAR(255) PRIMARY KEY,
//content TEXT NOT NULL,
//isPinned BOOLEAN NOT NULL
//);
//
//CREATE TABLE Comments (
//        id SERIAL PRIMARY KEY,
//        news_title VARCHAR(255) REFERENCES News(title),
//comment TEXT NOT NULL
//);
