import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestNewBookScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public RequestNewBookScreen(String username) {
        this.studentUsername = username;
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        setTitle("Request New Book - " + studentUsername);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Book Title:");
        JTextField titleField = new JTextField(15);
        JButton requestButton = new JButton("Request Book");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0;
        add(titleLabel, gbc);
        gbc.gridx = 1;
        add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(requestButton, gbc);
        gbc.gridy = 2;
        add(backButton, gbc);

        requestButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a book title.");
                return;
            }
            try {
                requestNewBook(title);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new StudentDashboard(studentUsername);
        });

        setVisible(true);
    }

    private int getStudentId() throws SQLException {
        String query = "SELECT s.student_id FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, studentUsername);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("student_id");
        }
        return -1;
    }

    private void requestNewBook(String title) throws SQLException {
        int studentId = getStudentId();
        if (studentId == -1) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        String insertQuery = "INSERT INTO book_requests (student_id, title, status, request_type, request_date) VALUES (?, ?, 'Pending', 'New', ?)";
        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        stmt.setInt(1, studentId);
        stmt.setString(2, title);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        stmt.setString(3, sdf.format(new Date()));
        stmt.executeUpdate();

        String notifQuery = "INSERT INTO notifications (user_id, type, message, status, created_at) " +
                "VALUES ((SELECT user_id FROM students WHERE student_id = ?), 'Approval', ?, 'Sent', ?)";
        PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
        notifStmt.setInt(1, studentId);
        notifStmt.setString(2, "New book request for '" + title + "' submitted.");
        notifStmt.setString(3, sdf.format(new Date()));
        notifStmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Book request submitted successfully.");
        dispose();
        new StudentDashboard(studentUsername);
    }
}
