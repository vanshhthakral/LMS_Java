import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnBooksPanel extends JPanel {
    private JTable issuedBooksTable;
    private DefaultTableModel model;

    public ReturnBooksPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Issued ID", "Student ID", "Book ID", "Issue Date", "Due Date"};
        model = new DefaultTableModel(columnNames, 0);
        issuedBooksTable = new JTable(model);
        add(new JScrollPane(issuedBooksTable), BorderLayout.CENTER);

        JButton returnBookButton = new JButton("Return Book & Calculate Fine");
        add(returnBookButton, BorderLayout.SOUTH);

        returnBookButton.addActionListener(e -> returnSelectedBook());

        loadIssuedBooks();
    }

    private void loadIssuedBooks() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {
            String query = "SELECT issued_id, student_id, book_id, issue_date, due_date FROM issued_books WHERE return_date IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("issued_id"),
                        rs.getInt("student_id"),
                        rs.getInt("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading issued books: " + e.getMessage());
        }
    }

    private void returnSelectedBook() {
        int selectedRow = issuedBooksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to return.");
            return;
        }

        int issueId = (int) model.getValueAt(selectedRow, 0);
        Date dueDate = (Date) model.getValueAt(selectedRow, 4);
        LocalDate today = LocalDate.now();
        LocalDate due = dueDate.toLocalDate();
        long daysLate = ChronoUnit.DAYS.between(due, today);
        double fine = daysLate > 0 ? daysLate * 2.0 : 0.0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {
            String updateQuery = "UPDATE issued_books SET return_date = ?, fine = ? WHERE issued_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setDate(1, Date.valueOf(today));
                stmt.setDouble(2, fine);
                stmt.setInt(3, issueId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book returned. Fine: ₹" + fine);
                loadIssuedBooks();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage());
        }
    }
}
