import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class NotificationsScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public NotificationsScreen(String username) {
        this.studentUsername = username;
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        setTitle("Notifications - " + studentUsername);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(notifArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new StudentDashboard(studentUsername);
        });
        add(backButton, BorderLayout.SOUTH);

        try {
            int userId = getUserId();
            if (userId == -1) {
                notifArea.setText("Student not found.");
                return;
            }

            String query = "SELECT type, message, status, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder notifications = new StringBuilder("📢 Notifications:\n\n");
            boolean hasNotifications = false;
            while (rs.next()) {
                hasNotifications = true;
                notifications.append("Type     : ").append(rs.getString("type"))
                             .append("\nMessage  : ").append(rs.getString("message"))
                             .append("\nStatus   : ").append(rs.getString("status"))
                             .append("\nDate     : ").append(rs.getString("created_at"))
                             .append("\n--------------------------------------\n");
            }

            if (!hasNotifications) {
                notifications.append("No notifications available.");
            }

            notifArea.setText(notifications.toString());

            String updateQuery = "UPDATE notifications SET status = 'Read' WHERE user_id = ? AND status = 'Sent'";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, userId);
            updateStmt.executeUpdate();
        } catch (SQLException ex) {
            notifArea.setText("Error loading notifications: " + ex.getMessage());
        }

        setVisible(true);
    }

    private int getUserId() throws SQLException {
        String query = "SELECT u.user_id FROM users u WHERE u.username = ? AND u.role = 'Student'";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, studentUsername);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
        return -1;
    }
}
