import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddBookPanel extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public AddBookPanel() {
        setTitle("Add/View/Delete Books");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Total Copies", "Available Copies"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Selected Book");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load books from database
        loadBooks();

        addButton.addActionListener(e -> addBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());

        setVisible(true);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books:\n" + e.getMessage());
        }
    }

    private void addBookDialog() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField copiesField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Book ID:"));
        panel.add(idField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Total Copies:"));
        panel.add(copiesField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String bookId = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String totalCopiesStr = copiesField.getText().trim();

            if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || totalCopiesStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                int bookIdInt = Integer.parseInt(bookId);
                int totalCopies = Integer.parseInt(totalCopiesStr);

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "INSERT INTO books (book_id, title, author, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)")) {
                    stmt.setInt(1, bookIdInt);
                    stmt.setString(2, title);
                    stmt.setString(3, author);
                    stmt.setInt(4, totalCopies);
                    stmt.setInt(5, totalCopies);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Book added successfully.");
                        loadBooks();  // refresh table
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Book ID and Total Copies must be valid numbers.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding book:\n" + ex.getMessage());
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            stmt.setInt(1, bookId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                loadBooks();  // refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Book not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting book:\n" + ex.getMessage());
        }
    }
}
