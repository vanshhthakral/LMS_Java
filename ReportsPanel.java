import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        reportArea.setEditable(false);

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");

            Statement stmt = conn.createStatement();

            // 1. Available Books (total copies - borrowed copies)
            ResultSet rs1 = stmt.executeQuery("SELECT SUM(total_copies) AS total_books FROM books");
            int totalBooks = rs1.next() ? rs1.getInt("total_books") : 0;

            // 2. Borrowed Books
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS borrowed FROM issued_books WHERE return_date IS NULL");
            int borrowedBooks = rs2.next() ? rs2.getInt("borrowed") : 0;

            int availableBooks = totalBooks - borrowedBooks;

            // 3. Total Fines Collected
            ResultSet rs3 = stmt.executeQuery("SELECT SUM(fine_amount) AS total_fines FROM fines");
            double totalFines = rs3.next() ? rs3.getDouble("total_fines") : 0;

            // 4. Total Users
            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) AS total_users FROM users");
            int totalUsers = rs4.next() ? rs4.getInt("total_users") : 0;

            String report = """
                --- Library Reports ---
                
                📚 Available Books    : %d
                📕 Borrowed Books     : %d
                💰 Total Fines Collected: ₹%.2f
                👥 Total Users        : %d
                """.formatted(availableBooks, borrowedBooks, totalFines, totalUsers);

            reportArea.setText(report);

            conn.close();
        } catch (Exception e) {
            reportArea.setText("Error fetching report:\n" + e.getMessage());
            e.printStackTrace();
        }

        add(new JScrollPane(reportArea), BorderLayout.CENTER);
    }
}
