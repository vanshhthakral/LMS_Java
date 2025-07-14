import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class IssueBookPanel extends JFrame {
    public IssueBookPanel() {
        setTitle("Issue Books");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));

        JLabel bookIdLabel = new JLabel("Book ID:");
        JTextField bookIdField = new JTextField();

        JLabel studentIdLabel = new JLabel("Student ID:");
        JTextField studentIdField = new JTextField();

        JLabel issueDateLabel = new JLabel("Issue Date (YYYY-MM-DD):");
        JTextField issueDateField = new JTextField(LocalDate.now().toString());

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        JTextField dueDateField = new JTextField(LocalDate.now().plusDays(14).toString());

        JButton issueButton = new JButton("Issue Book");
        issueButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {

                String bookId = bookIdField.getText();
                String studentId = studentIdField.getText();
                String issueDate = issueDateField.getText();
                String dueDate = dueDateField.getText();

                String sql = "INSERT INTO issued_books (book_id, student_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(bookId));
                stmt.setInt(2, Integer.parseInt(studentId));
                stmt.setDate(3, Date.valueOf(issueDate));
                stmt.setDate(4, Date.valueOf(dueDate));

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Book issued successfully!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error issuing book:\n" + ex.getMessage());
            }
        });

        add(bookIdLabel); add(bookIdField);
        add(studentIdLabel); add(studentIdField);
        add(issueDateLabel); add(issueDateField);
        add(dueDateLabel); add(dueDateField);
        add(new JLabel()); add(issueButton);

        setVisible(true);
    }
}
