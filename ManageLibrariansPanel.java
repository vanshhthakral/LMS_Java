import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageLibrariansPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageLibrariansPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Librarians", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"User ID", "Username", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Librarian");
        JButton toggleStatusBtn = new JButton("Active/Inactive");

        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn);
        btnPanel.add(toggleStatusBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadLibrarians());
        addBtn.addActionListener(e -> openAddLibrarianDialog());
        toggleStatusBtn.addActionListener(e -> toggleStatus());

        loadLibrarians();
    }

    private void loadLibrarians() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {

            String query = "SELECT user_id, username, email, phone, status FROM users WHERE role = 'librarian'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load librarians", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddLibrarianDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Librarian", true);
        dialog.setSize(350, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField passwordField = new JTextField();

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(saveBtn);
        dialog.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {

                String insert = "INSERT INTO users (username, email, phone, password, role, status) VALUES (?, ?, ?, ?, 'librarian', 'Active')";
                PreparedStatement stmt = conn.prepareStatement(insert);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Librarian added successfully.");
                dialog.dispose();
                loadLibrarians();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Failed to add librarian.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void toggleStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a librarian to toggle status.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        String newStatus = currentStatus.equalsIgnoreCase("Active") ? "Inactive" : "Active";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456")) {

            String update = "UPDATE users SET status = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Status updated to " + newStatus + ".");
            loadLibrarians();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
