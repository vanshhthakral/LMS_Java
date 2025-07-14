import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public UserManagementPanel() {
        setTitle("User Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("User Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"User ID", "Username", "Role", "Email", "Phone", "Active", "Toggle Active"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only 'Toggle Active' is editable (for button)
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        add(refreshButton, BorderLayout.SOUTH);

        // Add custom renderer/editor for button
        TableColumn toggleColumn = table.getColumnModel().getColumn(6);
        toggleColumn.setCellRenderer(new ButtonRenderer());
        toggleColumn.setCellEditor(new ButtonEditor(new JCheckBox(), table));

        loadUsers();
        setVisible(true);
    }

    private void loadUsers() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, username, role, email, phone, active FROM users")) {

            while (rs.next()) {
                String status = rs.getString("active").equalsIgnoreCase("Yes") ? "Yes" : "No";
                model.addRow(new Object[]{
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        status,
                        "Toggle"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserManagementPanel::new);
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                    boolean hasFocus, int row, int column) {
        setText("Toggle");
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "Toggle" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            int row = table.getSelectedRow();
            String userId = table.getValueAt(row, 0).toString();
            String currentStatus = table.getValueAt(row, 5).toString();
            toggleUserStatus(userId, currentStatus);
        }
        isPushed = false;
        return label;
    }

    private void toggleUserStatus(String userId, String currentStatus) {
        String newStatus = currentStatus.equalsIgnoreCase("Yes") ? "No" : "Yes";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_library_db", "root", "Rtyui@3456");
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET active = ? WHERE user_id = ?")) {
            stmt.setString(1, newStatus);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "User status updated to " + newStatus);
            table.setValueAt(newStatus, table.getSelectedRow(), 5); // Update the status cell visually
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating user status.");
        }
    }
}
