import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestHoldScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public RequestHoldScreen(String username) {
        this.studentUsername = username;
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        setTitle("Request Book Hold - " + studentUsername);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel bookIdLabel = new JLabel("Book ID:");
        JTextField bookIdField = new JTextField(15);
        JButton requestButton = new JButton("Request Hold");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0;
        add(bookIdLabel, gbc);
        gbc.gridx = 1;
        add(bookIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(requestButton, gbc);
        gbc.gridy = 2;
        add(backButton, gbc);

        requestButton.addActionListener(e -> {
            String bookId = bookIdField.getText().trim();
            if (bookId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Book ID.");
                return;
            }
            try {
                requestBookHold(bookId);
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

    private int getUserIdFromStudentId(int studentId) throws SQLException {
        String query = "SELECT user_id FROM students WHERE student_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
        return -1;
    }

    private void requestBookHold(String bookId) throws SQLException {
        int studentId = getStudentId();
        if (studentId == -1) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        String checkBookQuery = "SELECT book_id FROM books WHERE book_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkBookQuery);
        checkStmt.setString(1, bookId);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Book does not exist.");
            return;
        }

        String insertQuery = "INSERT INTO book_requests (student_id, book_id, status, request_type, request_date, hold_expiry_date) " +
                             "VALUES (?, ?, 'Pending', 'Hold', ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        stmt.setInt(1, studentId);
        stmt.setString(2, bookId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = sdf.format(new Date());
        String expiryDate = sdf.format(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // 7 days later
        stmt.setString(3, requestDate);
        stmt.setString(4, expiryDate);
        stmt.executeUpdate();

        int userId = getUserIdFromStudentId(studentId);
        String notifQuery = "INSERT INTO notifications (user_id, type, message, status, created_at) " +
                            "VALUES (?, 'Approval', ?, 'Sent', ?)";
        PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
        notifStmt.setInt(1, userId);
        notifStmt.setString(2, "Hold request for book ID " + bookId + " submitted.");
        notifStmt.setString(3, requestDate);
        notifStmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Hold request submitted successfully.\nHold expires on: " + expiryDate);
        dispose();
        new StudentDashboard(studentUsername);
    }
}
