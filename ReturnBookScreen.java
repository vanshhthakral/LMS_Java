import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnBookScreen extends JFrame {
    private JTextField bookIdField;
    private JButton returnButton;
    private String studentUsername;
    private Connection conn;

    public ReturnBookScreen(String studentUsername) {
        this.studentUsername = studentUsername;
        setTitle("Return Book & Pay Fine");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // DB connection
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.");
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bookIdLabel = new JLabel("Enter Book ID to Return:");
        bookIdField = new JTextField();
        returnButton = new JButton("Return Book");

        panel.add(bookIdLabel);
        panel.add(bookIdField);
        panel.add(new JLabel());
        panel.add(returnButton);

        add(panel);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = bookIdField.getText().trim();
                if (bookId.isEmpty()) {
                    JOptionPane.showMessageDialog(ReturnBookScreen.this, "Please enter Book ID.");
                    return;
                }
                try {
                    returnBook(bookId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ReturnBookScreen.this, "Error returning book.");
                }
            }
        });

        setVisible(true);
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

    private void returnBook(String bookIdStr) throws Exception {
        int studentId = getStudentId();
        if (studentId == -1) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        int bookId = Integer.parseInt(bookIdStr);

        String checkQuery = "SELECT borrow_id, due_date FROM borrowed_books WHERE book_id = ? AND student_id = ? AND status = 'Issued'";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setInt(1, bookId);
        checkStmt.setInt(2, studentId);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Book not borrowed or already returned.");
            return;
        }

        int recordId = rs.getInt("borrow_id");
        String dueDateStr = rs.getString("due_date");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDate = sdf.parse(dueDateStr);
        Date returnDate = new Date();

        long diffDays = (returnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
        double fineAmount = Math.max(0, diffDays * 10.0);

        String updateTransaction = "UPDATE borrowed_books SET return_date = ?, status = 'Returned' WHERE borrow_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateTransaction);
        updateStmt.setString(1, sdf.format(returnDate));
        updateStmt.setInt(2, recordId);
        updateStmt.executeUpdate();

        String updateBook = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
        PreparedStatement bookStmt = conn.prepareStatement(updateBook);
        bookStmt.setInt(1, bookId);
        bookStmt.executeUpdate();

        if (fineAmount > 0) {
            String fineInsert = "INSERT INTO fines (student_id, book_id, amount, status, issued_date) VALUES (?, ?, ?, 'Pending', ?)";
            PreparedStatement fineStmt = conn.prepareStatement(fineInsert);
            fineStmt.setInt(1, studentId);
            fineStmt.setInt(2, bookId);
            fineStmt.setDouble(3, fineAmount);
            fineStmt.setString(4, sdf.format(returnDate));
            fineStmt.executeUpdate();

            // Fetch user_id for notification
            String userIdQuery = "SELECT user_id FROM students WHERE student_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userIdQuery);
            userStmt.setInt(1, studentId);
            ResultSet userRs = userStmt.executeQuery();
            int userId = -1;
            if (userRs.next()) {
                userId = userRs.getInt("user_id");
            }

            if (userId != -1) {
                String notifInsert = "INSERT INTO notifications (user_id, type, message, status, created_at) VALUES (?, 'Fine', ?, 'Sent', NOW())";
                PreparedStatement notifStmt = conn.prepareStatement(notifInsert);
                notifStmt.setInt(1, userId);
                notifStmt.setString(2, "Fine of ₹" + fineAmount + " issued for late return.");
                notifStmt.executeUpdate();
            }
        }

        JOptionPane.showMessageDialog(this, "Book returned successfully!" +
                (fineAmount > 0 ? " Fine: ₹" + fineAmount : ""));
        dispose();
        new StudentDashboard(studentUsername);
    }
}
