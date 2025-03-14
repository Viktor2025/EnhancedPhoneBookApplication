import java.sql.*;

public class Connect {
    private static final String URL = "jdbc:mysql://localhost:3306/sakila1"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "viktor"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addContact(String name, String phone, String email, String address) {
        String checkQuery = "SELECT COUNT(*) FROM sakila1.contacts WHERE phone = ?";
        String insertQuery = "INSERT INTO sakila1.contacts (name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, phone);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("⚠️ Error: Phone number already exists!");
                return;
            }
            insertStmt.setString(1, name);
            insertStmt.setString(2, phone);
            insertStmt.setString(3, email);
            insertStmt.setString(4, address);
            insertStmt.executeUpdate();

            System.out.println("✅ Contact Added Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateContact(String name, String phone, String email, String address) {
        String query = "UPDATE sakila1.contacts SET Phone = ?, Email = ?, Address = ? WHERE Name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setString(3, address);
            pstmt.setString(4, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteContact(String name) {
        String query = "DELETE FROM sakila1.contacts WHERE Name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getContacts() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM sakila1.contacts");
    }
}
