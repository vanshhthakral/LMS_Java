import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginScreen extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JComboBox<String> roleBox;

    public LoginScreen() {
        setTitle("Smart Library Login");
        setSize(850, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel bgPanel = new BackgroundPanel("lib_bg.jpg");
        bgPanel.setLayout(new BorderLayout());
        setContentPane(bgPanel);

        JLabel titleLabel = new JLabel("Welcome to the Smart Library Management System!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        bgPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new RoundedPanel();
        formContainer.setBackground(new Color(255, 255, 255, 230));
        formContainer.setPreferredSize(new Dimension(350, 300));
        formContainer.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formContainer.add(new JLabel("Login as:"), gbc);

        roleBox = new JComboBox<>(new String[]{"Admin", "Librarian", "Student"});
        gbc.gridx = 1;
        formContainer.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formContainer.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        formContainer.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formContainer.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        formContainer.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formContainer.add(loginBtn, gbc);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(formContainer);
        bgPanel.add(centerWrapper, BorderLayout.CENTER);

        setVisible(true);
    }

    void login() {
        String user = usernameField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (role.equals("Admin")) {
            if (user.equals("admin") && pass.equals("admin123")) {
                JOptionPane.showMessageDialog(this, "Login successful as Admin");
                this.dispose();
                new AdminDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin credentials.");
            }
        } else if (role.equals("Librarian")) {
            if (user.equals("librarian") && pass.equals("libpass")) {
                JOptionPane.showMessageDialog(this, "Login successful as Librarian");
                this.dispose();
                new LibrarianDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Librarian credentials.");
            }
        } else if (role.equals("Student")) {
            new StudentLoginChoice();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid role selected.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}

// Custom background panel
class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String imagePath) {
        try {
            background = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Background image not found: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// Rounded panel
class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        g2.dispose();
        super.paintComponent(g);
    }
}

// Student role choice
class StudentLoginChoice extends JFrame {
    public StudentLoginChoice() {
        setTitle("Student Access");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JLabel prompt = new JLabel("Choose an option:");
        JButton existingBtn = new JButton("Login as Existing Student");
        JButton registerBtn = new JButton("Register as New Member");

        registerBtn.addActionListener(e -> {
            JFrame frame = new JFrame("Register New Member");
            frame.setContentPane(new RegisterMemberPanel());
            frame.setSize(400, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        existingBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard("student");
        });

      
        add(prompt);
        add(existingBtn);
        add(registerBtn);

        setVisible(true);
    }
}

// Student registration placeholder
class StudentRegistration extends JFrame {
    public StudentRegistration() {
        setTitle("New Student Registration");
        setSize(400, 300);
        setLocationRelativeTo(null);
        JLabel info = new JLabel("Student registration form coming soon...");
        add(info);
        setVisible(true);
    }
}
