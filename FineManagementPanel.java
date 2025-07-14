import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FineManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public FineManagementPanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Fine Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Fine ID", "Student ID", "Borrow ID", "Issue Date", "Due Date", "Days Overdue", "Fine Amount", "Status"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton markAsPaidBtn = new JButton("Mark as Paid");
        JButton printBtn = new JButton("Print Fines");

        markAsPaidBtn.addActionListener(e -> markSelectedAsPaid());
        printBtn.addActionListener(e -> printFinesToConsole());

        buttonPanel.add(markAsPaidBtn);
        buttonPanel.add(printBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFineData();
    }

    private void loadFineData() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fines WHERE status = 'Unpaid'")) {

            while (rs.next()) {
                String fineId = rs.getString("fine_id");
                String studentId = rs.getString("student_id");
                String borrowId = rs.getString("borrow_id");
                Date issueDate = rs.getDate("issue_date");
                Date dueDate = rs.getDate("due_date");
                long daysOverdue = rs.getLong("days_overdue");
                double amount = rs.getDouble("fine_amount");
                String status = rs.getString("status");

                model.addRow(new Object[]{fineId, studentId, borrowId, issueDate, dueDate, daysOverdue, amount, status});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading fine data.");
        }
    }

    private void markSelectedAsPaid() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to mark as paid.");
            return;
        }

        String fineId = model.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             PreparedStatement stmt = conn.prepareStatement("UPDATE fines SET status = 'Paid' WHERE fine_id = ?")) {

            stmt.setString(1, fineId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Fine marked as paid.");
            loadFineData(); // refresh table

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating fine status.");
        }
    }

    private void printFinesToConsole() {
        System.out.println("=== Fine Details ===");
        for (int i = 0; i < model.getRowCount(); i++) {
            String fineId = model.getValueAt(i, 0).toString();
            String studentId = model.getValueAt(i, 1).toString();
            String borrowId = model.getValueAt(i, 2).toString();
            String issueDate = model.getValueAt(i, 3).toString();
            String dueDate = model.getValueAt(i, 4).toString();
            String daysOverdue = model.getValueAt(i, 5).toString();
            String fineAmount = model.getValueAt(i, 6).toString();
            String status = model.getValueAt(i, 7).toString();

            System.out.printf("Fine ID: %s | Student ID: %s | Borrow ID: %s | Issue Date: %s | Due Date: %s | Days Overdue: %s | Fine Amount: %s | Status: %s%n",
                    fineId, studentId, borrowId, issueDate, dueDate, daysOverdue, fineAmount, status);
        }
    }
}
