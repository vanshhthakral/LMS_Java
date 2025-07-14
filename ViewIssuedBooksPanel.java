import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewIssuedBooksPanel extends JFrame {

    public ViewIssuedBooksPanel() {
        setTitle("View Issued Books");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Issued ID", "Book ID", "Student ID", "Issue Date", "Due Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
            String query = "SELECT issued_id, book_id, student_id, issue_date, due_date FROM issued_books";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int issueId = rs.getInt("issued_id");
                int bookId = rs.getInt("book_id");
                int studentId = rs.getInt("student_id");
                Date issueDate = rs.getDate("issue_date");
                Date dueDate = rs.getDate("due_date");

                model.addRow(new Object[]{issueId, bookId, studentId, issueDate, dueDate});
            }

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading issued books:\n" + e.getMessage());
            e.printStackTrace();
        }

        setVisible(true);
    }
}
