import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    private User loggedInUser;

    public boolean login(String username, String password, String role) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, username.trim());
            statement.setString(2, password.trim());
            statement.setString(3, role.trim());

            System.out.println("Attempting to log in with username: " + username + ", role: " + role);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                loggedInUser = new User(
                        rs.getString("id"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                return true;
            } else {
                System.out.println("Invalid credentials or role.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        loggedInUser = null;
        DBConnection.closeConnection();
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}