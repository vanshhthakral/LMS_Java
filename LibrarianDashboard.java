import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {
    public LibrarianDashboard(String librarianName) {
        setTitle("Librarian Dashboard - " + librarianName);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1, 10, 10));

        JButton addBookBtn = new JButton("Add/View/Delete Books");
        addBookBtn.addActionListener(e -> new AddBookPanel());
        add(addBookBtn);

        JButton issueBookBtn = new JButton("Issue Books");
        issueBookBtn.addActionListener(e -> new IssueBookPanel());
        add(issueBookBtn);

        JButton viewIssuedBooksBtn = new JButton("View Issued Books");
        viewIssuedBooksBtn.addActionListener(e -> new ViewIssuedBooksPanel());
        add(viewIssuedBooksBtn);

        JButton returnBooksBtn = new JButton("Return Books & Calculate Fines");
        returnBooksBtn.addActionListener(e -> {
        JFrame returnFrame = new JFrame("Return Books & Calculate Fines");
        returnFrame.setContentPane(new ReturnBooksPanel());
        returnFrame.setSize(800, 400);
        returnFrame.setLocationRelativeTo(null);
        returnFrame.setVisible(true);
        });
        add(returnBooksBtn);


        JButton manageStudentsBtn = new JButton("Student Records");
        manageStudentsBtn.addActionListener(e -> new ManageStudentPanel());
        add(manageStudentsBtn);

        JButton requestApprovalBtn = new JButton("Request Approval");
        requestApprovalBtn.addActionListener(e -> new RequestApprovalPanel());
        add(requestApprovalBtn);

        JButton viewOverdueBtn = new JButton("View Overdue Books");
        viewOverdueBtn.addActionListener(e -> new OverdueBooksPanel());
        add(viewOverdueBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
        add(logoutBtn);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarianDashboard("Ashika"));
    }
}
