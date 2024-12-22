import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Admin {
    private List<User> users;

    public void addUser(List<User> users) {
        String insertQuery = "INSERT INTO users (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                for (User user : users) {

                    pstmt.setString(1, user.getId());
                    pstmt.setString(2, user.getUserName());
                    pstmt.setString(3, user.getEmail());
                    pstmt.setString(4, user.getPassword());
                    pstmt.setString(5, user.getRole());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                connection.commit();
                System.out.println("Users added successfully.");
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error adding users. Transaction rolled back.");
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.out.println("Failed to add users.");
            e.printStackTrace();
        }
    }

    public void removeUser(User user) {
        String deleteQuery = "DELETE FROM users WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {

            pstmt.setString(1, user.getUserName());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User " + user.getUserName() + " removed successfully.");
            } else {
                System.out.println("User " + user.getUserName() + " not found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to remove user.");
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM users";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(selectQuery);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("id"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(user);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve users.");
            e.printStackTrace();
        }

        return users;
    }

    public void updateUser(User updatedUser) {
        String updateQuery = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            pstmt.setString(1, updatedUser.getId());
            pstmt.setString(2, updatedUser.getUserName());
            pstmt.setString(3, updatedUser.getEmail());
            pstmt.setString(4, updatedUser.getPassword());
            pstmt.setString(5, updatedUser.getRole());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User " + updatedUser.getUserName() + " updated successfully.");
            } else {
                System.out.println("User not found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to update user.");
            e.printStackTrace();
        }
    }

}
