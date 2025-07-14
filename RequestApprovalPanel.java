import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RequestApprovalPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public RequestApprovalPanel() {
        setTitle("Pending Requests for Approval");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"Request ID", "Student ID", "Book ID", "Request Type", "Status", "Request Date"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);

        loadPendingRequests();

        JPanel buttonPanel = new JPanel();
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");

        approveBtn.addActionListener(e -> updateRequestStatus("Approved"));
        rejectBtn.addActionListener(e -> updateRequestStatus("Rejected"));

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadPendingRequests() {
        model.setRowCount(0); // clear existing rows
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM book_requests WHERE status = 'Pending'")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getInt("student_id"),
                        rs.getInt("book_id"),
                        rs.getString("request_type"),
                        rs.getString("status"),
                        rs.getDate("request_date")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading requests: " + e.getMessage());
        }
    }

    private void updateRequestStatus(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to " + newStatus.toLowerCase());
            return;
        }

        int requestId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             PreparedStatement ps = conn.prepareStatement("UPDATE book_requests SET status = ? WHERE request_id = ?")) {

            ps.setString(1, newStatus);
            ps.setInt(2, requestId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Request " + newStatus.toLowerCase() + " successfully!");
                loadPendingRequests(); // refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
        }
    }
}
