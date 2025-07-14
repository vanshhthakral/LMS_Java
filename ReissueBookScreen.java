import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReissueBookScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public ReissueBookScreen(String username) {
        this.studentUsername = username;
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        setTitle("Reissue Book - " + studentUsername);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel bookIdLabel = new JLabel("Book ID:");
        JTextField bookIdField = new JTextField(15);
        JButton reissueButton = new JButton("Reissue Book");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0;
        add(bookIdLabel, gbc);
        gbc.gridx = 1;
        add(bookIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(reissueButton, gbc);
        gbc.gridy = 2;
        add(backButton, gbc);

        reissueButton.addActionListener(e -> {
            String bookId = bookIdField.getText().trim();
            if (bookId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Book ID.");
                return;
            }
            try {
                reissueBook(bookId);
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

    private void reissueBook(String bookId) throws SQLException {
        int studentId = getStudentId();
        if (studentId == -1) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        // Check if book is issued to this student
        String checkQuery = "SELECT borrow_id, due_date FROM borrowed_books WHERE book_id = ? AND student_id = ? AND return_date IS NULL";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, bookId);
        checkStmt.setInt(2, studentId);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "This book is not currently borrowed by you or has already been returned.");
            return;
        }

        int borrowId = rs.getInt("borrow_id");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String newDueDate = sdf.format(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // +7 days

        // Update due date
        String updateQuery = "UPDATE borrowed_books SET due_date = ? WHERE borrow_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setString(1, newDueDate);
        updateStmt.setInt(2, borrowId);
        updateStmt.executeUpdate();

        // Notification
        int userId = getUserIdFromStudentId(studentId);
        String notifQuery = "INSERT INTO notifications (user_id, type, message, status, created_at) VALUES (?, 'Due Date', ?, 'Sent', ?)";
        PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
        notifStmt.setInt(1, userId);
        notifStmt.setString(2, "Book ID " + bookId + " reissued. New due date: " + newDueDate);
        notifStmt.setString(3, sdf.format(new Date()));
        notifStmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Book reissued successfully.\nNew due date: " + newDueDate);
        dispose();
        new StudentDashboard(studentUsername);
    }
}
