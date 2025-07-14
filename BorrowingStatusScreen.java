import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BorrowingStatusScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public BorrowingStatusScreen(String username) {
        this.studentUsername = username;

        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        setTitle("Borrowing Status - " + studentUsername);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new StudentDashboard(studentUsername);
        });
        add(backButton, BorderLayout.SOUTH);

        displayBorrowingStatus(statusArea);

        setVisible(true);
    }

    private void displayBorrowingStatus(JTextArea statusArea) {
        try {
            int studentId = getStudentId();
            System.out.println("Fetched student ID: " + studentId);  // Debug line

            if (studentId == -1) {
                statusArea.setText("Student not found.");
                return;
            }

            String query = "SELECT bb.borrow_id, b.title, bb.issue_date, bb.due_date, bb.return_date, bb.status " +
                           "FROM borrowed_books bb " +
                           "JOIN books b ON bb.book_id = b.book_id " +
                           "WHERE bb.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder status = new StringBuilder("Borrowing Status:\n\n");
            boolean found = false;

            while (rs.next()) {
                found = true;
                status.append("Book: ").append(rs.getString("title"))
                      .append("\nBorrow ID: ").append(rs.getInt("borrow_id"))
                      .append("\nIssue Date: ").append(rs.getString("issue_date"))
                      .append("\nDue Date: ").append(rs.getString("due_date"))
                      .append("\nReturn Date: ").append(rs.getString("return_date") == null ? "Not Returned" : rs.getString("return_date"))
                      .append("\nStatus: ").append(rs.getString("status"))
                      .append("\n------------------------------\n");
            }

            if (!found) {
                System.out.println("No borrowed books found for student ID: " + studentId);  // Debug
                statusArea.setText("No borrowed books found for this student.");
            } else {
                statusArea.setText(status.toString());
            }

        } catch (SQLException ex) {
            statusArea.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getStudentId() throws SQLException {
        String query = "SELECT student_id FROM students WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, studentUsername);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("student_id");
        }
        return -1;
    }
}
