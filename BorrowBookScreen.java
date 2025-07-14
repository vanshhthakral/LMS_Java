import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BorrowBookScreen extends JFrame {
    private String studentUsername;
    private Connection conn;

    public BorrowBookScreen(String username) {
        this.studentUsername = username;

        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return;
        }

        setTitle("Borrow Book");
        setSize(400, 250);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblBookId = new JLabel("Enter Book ID:");
        JTextField txtBookId = new JTextField(15);
        JButton btnBorrow = new JButton("Borrow");
        JButton btnBack = new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblBookId, gbc);

        gbc.gridx = 1;
        add(txtBookId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(btnBorrow, gbc);

        gbc.gridy = 2;
        add(btnBack, gbc);

        btnBorrow.addActionListener(e -> {
            String bookId = txtBookId.getText().trim();
            if (!bookId.isEmpty()) {
                try {
                    borrowBook(bookId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a Book ID.");
            }
        });

        btnBack.addActionListener(e -> {
            dispose();
            new StudentDashboard(studentUsername);
        });

        setVisible(true);
    }

    private int getStudentId() throws SQLException {
        String query = "SELECT s.student_id FROM students s " +
                       "JOIN users u ON s.user_id = u.user_id " +
                       "WHERE LOWER(u.username) = LOWER(?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, studentUsername);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("student_id");
        } else {
            return -1; // not found
        }
    }

    private void borrowBook(String bookId) throws SQLException {
        int studentId = getStudentId();
        if (studentId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Student record not found for username: " + studentUsername +
                    "\nPlease contact administrator to link student details.");
            return;
        }

        // Check book availability
        PreparedStatement checkBook = conn.prepareStatement(
                "SELECT available_copies FROM books WHERE book_id = ?");
        checkBook.setString(1, bookId);
        ResultSet rs = checkBook.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Book not found.");
            return;
        }

        int available = rs.getInt("available_copies");
        if (available <= 0) {
            JOptionPane.showMessageDialog(this, "No available copies.");
            return;
        }

        // Prepare borrow entry
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String issueDate = sdf.format(new Date());
        String dueDate = sdf.format(new Date(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)));
        String borrowDate = issueDate;

        PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO borrowed_books (student_id, book_id, issue_date, due_date, status, borrow_date) " +
                "VALUES (?, ?, ?, ?, 'Issued', ?)");
        insert.setInt(1, studentId);
        insert.setString(2, bookId);
        insert.setString(3, issueDate);
        insert.setString(4, dueDate);
        insert.setString(5, borrowDate);
        insert.executeUpdate();

        PreparedStatement update = conn.prepareStatement(
                "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?");
        update.setString(1, bookId);
        update.executeUpdate();

        JOptionPane.showMessageDialog(this, "Book borrowed successfully! Due by " + dueDate);
        dispose();
        new StudentDashboard(studentUsername);
    }
}
