import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardOverviewPanel extends JPanel {

    public DashboardOverviewPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        JLabel heading = new JLabel("\uD83D\uDCCA Admin Dashboard Overview", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(new Color(33, 53, 85));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(6, 1, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JLabel librariansLabel = createStatLabel("Total Librarians", "...");
        JLabel studentsLabel = createStatLabel("Total Students", "...");
        JLabel issuedTodayLabel = createStatLabel("Books Issued Today", "...");
        JLabel pendingReturnsLabel = createStatLabel("Pending Returns", "...");
        JLabel availableBooksLabel = createStatLabel("Available Books", "...");
        JLabel borrowedBooksLabel = createStatLabel("Borrowed Books", "...");

        statsPanel.add(librariansLabel);
        statsPanel.add(studentsLabel);
        statsPanel.add(issuedTodayLabel);
        statsPanel.add(pendingReturnsLabel);
        statsPanel.add(availableBooksLabel);
        statsPanel.add(borrowedBooksLabel);
        add(statsPanel, BorderLayout.CENTER);

        String url = "jdbc:mysql://localhost:3306/smart_library_db";
        String user = "root";
        String password = "Rtyui@3456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS total_librarians FROM users WHERE role = 'Librarian'");
            if (rs1.next()) librariansLabel.setText(createStatHTML("Total Librarians", rs1.getInt(1)));

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM students");
            if (rs2.next()) studentsLabel.setText(createStatHTML("Total Students", rs2.getInt(1)));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM issued_books WHERE issue_date = CURDATE()");
            if (rs3.next()) issuedTodayLabel.setText(createStatHTML("Books Issued Today", rs3.getInt(1)));

            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL AND due_date < CURDATE()");
            if (rs4.next()) pendingReturnsLabel.setText(createStatHTML("Pending Returns", rs4.getInt(1)));

            ResultSet rs5 = stmt.executeQuery("SELECT SUM(total_copies) - COUNT(ib.book_id) AS available FROM books b LEFT JOIN issued_books ib ON b.book_id = ib.book_id AND ib.return_date IS NULL");
            if (rs5.next()) availableBooksLabel.setText(createStatHTML("Available Books", rs5.getInt(1)));

            ResultSet rs6 = stmt.executeQuery("SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL");
            if (rs6.next()) borrowedBooksLabel.setText(createStatHTML("Borrowed Books", rs6.getInt(1)));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(createStatHTML(title, value), SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setOpaque(true);
        label.setBackground(new Color(240, 248, 255));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return label;
    }

    private String createStatHTML(String title, Object value) {
        return "<html><b style='color:#1a237e'>" + title + ":</b> <span style='color:#2e7d32'>" + value + "</span></html>";
    }
}
