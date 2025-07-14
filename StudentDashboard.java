import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1));

        JButton registerBtn = new JButton("Register as New Member");
registerBtn.addActionListener(e -> {
    JFrame frame = new JFrame("Register New Member");
    frame.setContentPane(new RegisterMemberPanel());
    frame.setSize(400, 250);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
});

        JButton borrowButton = new JButton("Borrow Books");
        borrowButton.addActionListener(e -> new BorrowBookScreen(username));
        add(borrowButton);

        JButton returnButton = new JButton("Return Books & Pay Fines");
        returnButton.addActionListener(e -> new ReturnBookScreen(username));
        add(returnButton);

        // JButton statusButton = new JButton("View Borrowing Status");
        // statusButton.addActionListener(e -> new BorrowingStatusScreen(username));
        // add(statusButton);

        JButton requestNewButton = new JButton("Request New Books");
        requestNewButton.addActionListener(e -> new RequestNewBookScreen(username));
        add(requestNewButton);

        JButton holdButton = new JButton("Request Hold");
        holdButton.addActionListener(e -> new RequestHoldScreen(username));
        add(holdButton);

        JButton reissueButton = new JButton("Reissue Books");
        reissueButton.addActionListener(e -> new ReissueBookScreen(username));
        add(reissueButton);

        JButton notificationsButton = new JButton("View Notifications");
        notificationsButton.addActionListener(e -> new NotificationsScreen(username));
        add(notificationsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
        add(logoutButton);

        setVisible(true);
    }
}