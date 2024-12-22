import java.sql.*;
import java.util.Date;

public class Message {
    private String sender;
    private String recipient;
    private String content;
    private Date timestamp;

    // Constructor
    public Message(String sender, String recipient, String content, Date timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // Setter
    public void setContent(String content) {
        this.content = content;
    }

    // Method to format the message
    public String formatMessage() {
        return "From: " + sender + "\nTo: " + recipient + "\nTime: " + timestamp + "\nMessage: " + content;
    }

    // JDBC Methods
    public void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO Messages (sender, recipient, content, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, content);
            stmt.setTimestamp(4, new Timestamp(timestamp.getTime()));
            stmt.executeUpdate();
        }
    }

    public static Message fetchFromDatabase(int id) throws SQLException {
        String sql = "SELECT sender, recipient, content, timestamp FROM Messages WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String sender = rs.getString("sender");
                    String recipient = rs.getString("recipient");
                    String content = rs.getString("content");
                    Date timestamp = new Date(rs.getTimestamp("timestamp").getTime());
                    return new Message(sender, recipient, content, timestamp);
                }
            }
        }
        return null;
    }

    public void updateContentInDatabase() throws SQLException {
        String sql = "UPDATE Messages SET content = ? WHERE sender = ? AND recipient = ? AND timestamp = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, content);
            stmt.setString(2, sender);
            stmt.setString(3, recipient);
            stmt.setTimestamp(4, new Timestamp(timestamp.getTime()));
            stmt.executeUpdate();
        }
    }

    public void deleteFromDatabase() throws SQLException {
        String sql = "DELETE FROM Messages WHERE sender = ? AND recipient = ? AND timestamp = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setTimestamp(3, new Timestamp(timestamp.getTime()));
            stmt.executeUpdate();
        }
    }
}