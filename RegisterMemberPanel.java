import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterMemberPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterMemberPanel() {
        setLayout(new GridLayout(4, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerUser());

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // empty cell
        add(registerButton);
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/smart_library_db";
        String dbUser = "root";
        String dbPassword = "Rtyui@3456";

        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Member registered successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering member: " + ex.getMessage());
        }
    }
}
