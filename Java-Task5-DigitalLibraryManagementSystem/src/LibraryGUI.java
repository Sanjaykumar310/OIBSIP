import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LibraryGUI {
    private static final String DATA = "library-data.ser";
    private static final String ADMIN = "admin";
    private static final String ADMIN_PASS = "admin123";

    private Library lib;
    private JFrame frame;
    private CardLayout cards;
    private JPanel mainPanel;
    private LibraryUser loggedUser; // null for admin

    public LibraryGUI() {
        lib = Library.load(DATA);
        lib.seedIfEmpty();
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Digital Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);
        mainPanel.add(loginPanel(), "login");
        mainPanel.add(adminPanel(), "admin");
        mainPanel.add(userPanel(), "user");

        frame.add(mainPanel);
        cards.show(mainPanel, "login");
        frame.setVisible(true);
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        JTextField tfUser = new JTextField(15);
        JLabel lblPass = new JLabel("Password:");
        JPasswordField pfPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUser, gbc);
        gbc.gridx = 1; panel.add(tfUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPass, gbc);
        gbc.gridx = 1; panel.add(pfPass, gbc);
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 2; panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());
            if (ADMIN.equals(user) && ADMIN_PASS.equals(pass)) {
                loggedUser = null; // admin mode
                cards.show(mainPanel, "admin");
            } else {
                LibraryUser lu = lib.findUserByUsername(user);
                if (lu != null && lu.verifyPassword(pass)) {
                    loggedUser = lu;
                    cards.show(mainPanel, "user");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return panel;
    }

    // ---------- Admin UI ----------
    private JPanel adminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel nav = new JPanel(new GridLayout(0,1,5,5));
        JButton btnView = new JButton("View Books");
        JButton btnSearch = new JButton("Search Books");
        JButton btnAdd = new JButton("Add Book");
        JButton btnUpdate = new JButton("Update Book");
        JButton btnDelete = new JButton("Delete Book");
        JButton btnLogout = new JButton("Logout");
        nav.add(btnView);
        nav.add(btnSearch);
        nav.add(btnAdd);
        nav.add(btnUpdate);
        nav.add(btnDelete);
        nav.add(btnLogout);
        panel.add(nav, BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());
        panel.add(content, BorderLayout.CENTER);

        // shared table model
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Title","Author","Category","Status"}, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        content.add(scroll, BorderLayout.CENTER);

        // Helper to refresh table
        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Book b : lib.getBooks()) { // direct access for simplicity
                model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable()?"Available":"Borrowed by "+b.getBorrowedBy()});
            }
        };

        btnView.addActionListener(e -> refresh.run());

        btnSearch.addActionListener(e -> {
            String query = JOptionPane.showInputDialog(frame, "Enter search term (title/author/category):");
            if (query != null) {
                model.setRowCount(0);
                for (Book b : lib.getBooks()) {
                    if (b.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        b.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                        b.getCategory().toLowerCase().contains(query.toLowerCase())) {
                        model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable()?"Available":"Borrowed by "+b.getBorrowedBy()});
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> {
            JTextField tfId = new JTextField();
            JTextField tfTitle = new JTextField();
            JTextField tfAuthor = new JTextField();
            JTextField tfCat = new JTextField();
            Object[] fields = {"ID (int)", tfId, "Title", tfTitle, "Author", tfAuthor, "Category", tfCat};
            int ok = JOptionPane.showConfirmDialog(frame, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                try {
                    int id = Integer.parseInt(tfId.getText().trim());
                    boolean added = lib.addBook(new Book(id, tfTitle.getText().trim(), tfAuthor.getText().trim(), tfCat.getText().trim()));
                    if (added) { lib.save(DATA); refresh.run(); JOptionPane.showMessageDialog(frame, "Book added."); }
                    else JOptionPane.showMessageDialog(frame, "Book ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame, "Enter Book ID to update:");
            if (sid == null) return;
            try {
                int id = Integer.parseInt(sid.trim());
                Book b = lib.findBookById(id);
                if (b == null) { JOptionPane.showMessageDialog(frame, "Book not found."); return; }
                JTextField tfTitle = new JTextField(b.getTitle());
                JTextField tfAuthor = new JTextField(b.getAuthor());
                JTextField tfCat = new JTextField(b.getCategory());
                Object[] fields = {"New Title", tfTitle, "New Author", tfAuthor, "New Category", tfCat};
                int ok = JOptionPane.showConfirmDialog(frame, fields, "Update Book", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    boolean okUpd = lib.updateBook(id, tfTitle.getText().trim(), tfAuthor.getText().trim(), tfCat.getText().trim());
                    if (okUpd) { lib.save(DATA); refresh.run(); JOptionPane.showMessageDialog(frame, "Book updated."); }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame, "Enter Book ID to delete:");
            if (sid == null) return;
            try {
                int id = Integer.parseInt(sid.trim());
                boolean okDel = lib.deleteBook(id);
                if (okDel) { lib.save(DATA); refresh.run(); JOptionPane.showMessageDialog(frame, "Book deleted."); }
                else JOptionPane.showMessageDialog(frame, "Cannot delete: book not found or currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLogout.addActionListener(e -> {
            lib.save(DATA);
            cards.show(mainPanel, "login");
        });

        // initial view
        refresh.run();
        return panel;
    }

    // ---------- User UI ----------
    private JPanel userPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel nav = new JPanel(new GridLayout(0,1,5,5));
        JButton btnBrowse = new JButton("Browse Books");
        JButton btnSearch = new JButton("Search Books");
        JButton btnBorrow = new JButton("Borrow Book");
        JButton btnReturn = new JButton("Return Book");
        JButton btnReserve = new JButton("Reserve Book");
        JButton btnFines = new JButton("Fine Management");
        JButton btnLogout = new JButton("Logout");
        nav.add(btnBrowse);
        nav.add(btnSearch);
        nav.add(btnBorrow);
        nav.add(btnReturn);
        nav.add(btnReserve);
        nav.add(btnFines);
        nav.add(btnLogout);
        panel.add(nav, BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());
        panel.add(content, BorderLayout.CENTER);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Title","Author","Category","Status"}, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        content.add(scroll, BorderLayout.CENTER);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Book b : lib.getBooks()) {
                model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable()?"Available":"Borrowed by "+b.getBorrowedBy()});
            }
        };

        btnBrowse.addActionListener(e -> refresh.run());

        btnSearch.addActionListener(e -> {
            String query = JOptionPane.showInputDialog(frame, "Enter search term:");
            if (query != null) {
                model.setRowCount(0);
                for (Book b : lib.getBooks()) {
                    if (b.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        b.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                        b.getCategory().toLowerCase().contains(query.toLowerCase())) {
                        model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable()?"Available":"Borrowed by "+b.getBorrowedBy()});
                    }
                }
            }
        });

        btnBorrow.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame, "Enter Book ID to borrow:");
            if (sid == null) return;
            try {
                int id = Integer.parseInt(sid.trim());
                boolean ok = lib.borrowBook(id, loggedUser.getUsername());
                if (ok) { lib.save(DATA); refresh.run(); JOptionPane.showMessageDialog(frame, "Book issued for 14 days."); }
                else JOptionPane.showMessageDialog(frame, "Borrow failed: check ID, account and availability.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReturn.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame, "Enter Book ID to return:");
            if (sid == null) return;
            try {
                int id = Integer.parseInt(sid.trim());
                double fine = lib.returnBook(id, loggedUser.getUsername());
                if (fine < 0) {
                    JOptionPane.showMessageDialog(frame, "Return failed: this book is not borrowed by you.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    lib.save(DATA);
                    refresh.run();
                    JOptionPane.showMessageDialog(frame, String.format("Book returned. Fine added: Rs. %.2f", fine));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReserve.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame, "Enter Book ID to reserve (must be borrowed):");
            if (sid == null) return;
            try {
                int id = Integer.parseInt(sid.trim());
                boolean ok = lib.reserveBook(id, loggedUser.getUsername());
                if (ok) { lib.save(DATA); JOptionPane.showMessageDialog(frame, "Reservation added."); }
                else JOptionPane.showMessageDialog(frame, "Reservation failed: book must be borrowed and not already reserved by you.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnFines.addActionListener(e -> {
            double bal = loggedUser.getFineBalance();
            JOptionPane.showMessageDialog(frame, String.format("Outstanding fine: Rs. %.2f", bal));
            if (bal > 0) {
                int ans = JOptionPane.showConfirmDialog(frame, "Pay full amount?", "Fine Payment", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    boolean paid = lib.payFine(loggedUser.getUsername(), bal);
                    if (paid) { lib.save(DATA); JOptionPane.showMessageDialog(frame, "Fine paid."); }
                    else JOptionPane.showMessageDialog(frame, "Payment failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLogout.addActionListener(e -> {
            lib.save(DATA);
            loggedUser = null;
            cards.show(mainPanel, "login");
        });

        refresh.run();
        return panel;
    }

    // Entry point wrapper (called from Main)
    public static void launch() {
        SwingUtilities.invokeLater(() -> new LibraryGUI());
    }
}
