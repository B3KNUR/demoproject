import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String topic;
    private Date date;
    private String room;

    public Lesson(String topic, Date date, String room) {
        this.topic = topic;
        this.date = date;
        this.room = room;
    }

    public String getDetails() {
        return "Topic: " + topic + ", Date: " + date + ", Room: " + room;
    }

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    // JDBC Methods
    public static void addLesson(Lesson lesson) {
        String insertQuery = "INSERT INTO lessons (topic, date, room) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {

            pstmt.setString(1, lesson.getTopic());
            pstmt.setDate(2, lesson.getDate());
            pstmt.setString(3, lesson.getRoom());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Lesson added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add lesson.");
            e.printStackTrace();
        }
    }

    public static List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        String selectQuery = "SELECT * FROM lessons";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(selectQuery);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Lesson lesson = new Lesson(
                        rs.getString("topic"),
                        rs.getDate("date"),
                        rs.getString("room")
                );
                lessons.add(lesson);
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve lessons.");
            e.printStackTrace();
        }

        return lessons;
    }

    public static void updateLesson(String topic, Lesson updatedLesson) {
        String updateQuery = "UPDATE lessons SET topic = ?, date = ?, room = ? WHERE topic = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            pstmt.setString(1, updatedLesson.getTopic());
            pstmt.setDate(2, updatedLesson.getDate());
            pstmt.setString(3, updatedLesson.getRoom());
            pstmt.setString(4, topic);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Lesson updated successfully.");
            } else {
                System.out.println("Lesson not found.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to update lesson.");
            e.printStackTrace();
        }
    }

    public static void deleteLesson(String topic) {
        String deleteQuery = "DELETE FROM lessons WHERE topic = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {

            pstmt.setString(1, topic);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Lesson deleted successfully.");
            } else {
                System.out.println("Lesson not found.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to delete lesson.");
            e.printStackTrace();
        }
    }
}
