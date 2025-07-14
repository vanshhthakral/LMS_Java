import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class SettingsPanel extends JPanel {
    private boolean darkModeEnabled = false;
    private JFrame parentFrame;

    public SettingsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Settings"));

        JButton darkModeButton = new JButton("Toggle Dark Mode");
        JButton backupButton = new JButton("Backup Database");
        JButton changePasswordButton = new JButton("Change Password");
        JLabel note = new JLabel("Note: Full features coming soon...", JLabel.CENTER);

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        darkModeButton.setFont(buttonFont);
        backupButton.setFont(buttonFont);
        changePasswordButton.setFont(buttonFont);
        note.setFont(new Font("Arial", Font.ITALIC, 12));

        add(darkModeButton);
        add(backupButton);
        add(changePasswordButton);
        add(note);

        // Actions
        darkModeButton.addActionListener(e -> toggleDarkMode());
        backupButton.addActionListener(e -> backupDatabase());
        changePasswordButton.addActionListener(e -> openChangePasswordDialog());
    }

    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        Color bg = darkModeEnabled ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkModeEnabled ? Color.WHITE : Color.BLACK;

        setComponentColors(this, bg, fg);
        repaint();
        JOptionPane.showMessageDialog(this, "Dark Mode " + (darkModeEnabled ? "Enabled" : "Disabled"));
    }

    private void setComponentColors(Container container, Color bg, Color fg) {
        for (Component comp : container.getComponents()) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof Container) {
                setComponentColors((Container) comp, bg, fg);
            }
        }
    }

    private void backupDatabase() {
        try {
            String dbName = "smart_library_db";
            String user = "root";
            String password = "Rtyui@3456";
            String backupFile = "backup_" + System.currentTimeMillis() + ".sql";

            String command = String.format("mysqldump -u%s -p%s %s", user, password, dbName);
            Process process = Runtime.getRuntime().exec(command);

            try (InputStream is = process.getInputStream(); 
                 FileOutputStream fos = new FileOutputStream(backupFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JOptionPane.showMessageDialog(this, "Backup successful: " + new File(backupFile).getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, "Backup failed. Make sure mysqldump is in PATH.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Backup Error: " + e.getMessage());
        }
    }

    private void openChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField oldPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Old Password:"));
        panel.add(oldPassField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String user = usernameField.getText().trim();
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());

            if (user.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String checkQuery = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, user);
                checkStmt.setString(2, oldPass);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    String updateQuery = "UPDATE users SET password=? WHERE username=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setString(1, newPass);
                    updateStmt.setString(2, user);
                    updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Password updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or old password.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
            }
        }
    }
}
