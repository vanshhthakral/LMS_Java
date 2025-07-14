import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard(String adminName) {
        setTitle("Admin Dashboard - " + adminName);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1, 10, 10));

        JButton dashboardBtn = new JButton("Dashboard Overview");
        dashboardBtn.addActionListener(e -> openFrame("Dashboard Overview", new DashboardOverviewPanel()));
        add(dashboardBtn);

        JButton manageLibrariansBtn = new JButton("Manage Librarians");
        manageLibrariansBtn.addActionListener(e -> openFrame("Manage Librarians", new ManageLibrariansPanel()));
        add(manageLibrariansBtn);

        JButton userMgmtBtn = new JButton("User Management");
        userMgmtBtn.addActionListener(e -> openFrame("User Management", new UserManagementPanel()));
        add(userMgmtBtn);

        JButton fineMgmtBtn = new JButton("Fine Management");
fineMgmtBtn.addActionListener(e -> openFrame("Fine Management", new FineManagementPanel()));
add(fineMgmtBtn);


        JButton reportsBtn = new JButton("Reports");
        reportsBtn.addActionListener(e -> openFrame("Reports", new ReportsPanel()));
        add(reportsBtn);

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> openFrame("Settings", new SettingsPanel(this)));
        add(settingsBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen(); // assuming LoginScreen is your main login page
        });
        add(logoutBtn);

        setVisible(true);
    }

    private void openFrame(String title, JPanel panel) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(panel);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("admin"));
    }
}
