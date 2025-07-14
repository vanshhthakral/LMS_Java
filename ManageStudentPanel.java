import javax.swing.*;
import javax.swing.table.DefaultTableModel; // ✅ Required for DefaultTableModel
import java.awt.*;
import java.sql.*;

public class ManageStudentPanel extends JFrame {
    public ManageStudentPanel() {
        setTitle("Student Records");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Student ID", "User-name", "Department", "Year"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, username, department, year FROM students")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("username"),
                    rs.getString("department"),
                    rs.getInt("year")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}
