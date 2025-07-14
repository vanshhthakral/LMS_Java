import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OverdueBooksPanel extends JFrame {
    public OverdueBooksPanel() {
        setTitle("Overdue Books");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"Issue ID", "Student ID", "Book ID", "Issue Date", "Due Date", "Return Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM issued_books WHERE due_date < CURDATE() AND return_date IS NULL"
             )) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("issued_id"),
                        rs.getInt("student_id"),
                        rs.getInt("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        "Not Returned"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching overdue books: " + e.getMessage());
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}
