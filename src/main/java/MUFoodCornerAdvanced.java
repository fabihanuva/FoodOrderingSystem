import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Custom Rounded Border for professional look
class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color color;
    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }
}

// Entity Classes
class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    static int nextId = 1;
    int id;
    String name;
    int price;
    int quantity;
    public MenuItem(String name, int price) {
        this.id = nextId++;
        this.name = name; this.price = price; this.quantity = 0;
    }
    public int getSubtotal() { return price * quantity; }
}

class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    ArrayList<MenuItem> items;
    String name;
    String phone;
    String address;
    LocalDateTime timestamp;
    String status;
    int discount;
    int total;

    public Order(ArrayList<MenuItem> items, String name, String phone, String address, int discount) {
        this.items = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.quantity > 0) {
                MenuItem copy = new MenuItem(item.name, item.price);
                copy.quantity = item.quantity;
                this.items.add(copy);
            }
        }
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.timestamp = LocalDateTime.now();
        this.status = "Pending";
        this.discount = discount;
        this.total = calculateTotal();
    }

    private int calculateTotal() {
        int t = 0;
        for (MenuItem item : items) t += item.getSubtotal();
        return t - discount;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("🕒 Order Time: ").append(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("👤 Customer: ").append(name).append(" (").append(phone).append(")\n");
        for (MenuItem item : items) sb.append("• ").append(item.name).append(" x").append(item.quantity).append(" = TK ").append(item.getSubtotal()).append("\n");
        if (discount > 0) sb.append("🎁 Discount: -TK ").append(discount).append("\n");
        sb.append("📍 Address: ").append(address).append("\n");
        sb.append("💰 Total: TK ").append(total).append("\n");
        sb.append("📋 Status: ").append(status).append("\n");
        return sb.toString();
    }
}

public class MUFoodCornerAdvanced extends JFrame {
    // UI Constants
    private static final Color APP_BG = new Color(248, 249, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT = new Color(255, 107, 107); // Coral Red
    private static final Color SECONDARY = new Color(32, 201, 151); // Teal
    private static final Color NEUTRAL = new Color(52, 58, 64);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_REG = new Font("Segoe UI", Font.PLAIN, 14);

    private ArrayList<MenuItem> menuItems;
    private ArrayList<Order> orders;
    private JPanel menuDisplayPanel;
    private JTextField nameField, phoneField, addressField, promoField;
    private JTextArea summaryArea;
    private JLabel totalLabel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private boolean adminUnlocked = false;

    public MUFoodCornerAdvanced() {
        super("MU Food Corner Elite");
        loadData();
        setupFrame();
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.addTab("🛒 SHOP", createOrderPanel());
        tabs.addTab("📜 HISTORY", createHistoryPanel());
        tabs.addTab("⚙️ ADMIN", createAdminPanel());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2 && !adminUnlocked) {
                String pass = JOptionPane.showInputDialog(this, "Enter admin password:");
                if (pass == null || !pass.equals("admin123")) {
                    tabs.setSelectedIndex(0);
                    if (pass != null) JOptionPane.showMessageDialog(this, "Incorrect password.");
                } else {
                    adminUnlocked = true;
                }
            }
        });
        add(tabs);
        setVisible(true);
    }

    private void setupFrame() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(1100, 820));
        setLocationRelativeTo(null);
        getContentPane().setBackground(APP_BG);
        setLocationRelativeTo(null);
    }

    private JPanel createOrderPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(APP_BG);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Header (Banner)
        JPanel header = createStyledCard();
        header.setBackground(ACCENT);
        header.setLayout(new BorderLayout());
        JLabel title = new JLabel("MU FOOD CORNER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(header, gbc);

        // 2. Menu Section (Left)
        JPanel menuCard = createStyledCard();
        menuCard.setLayout(new BorderLayout(10, 10));
        JLabel mTitle = new JLabel("🔥 Fresh Menu", SwingConstants.LEFT);
        mTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        menuCard.add(mTitle, BorderLayout.NORTH);

        menuDisplayPanel = new JPanel();
        menuDisplayPanel.setLayout(new BoxLayout(menuDisplayPanel, BoxLayout.Y_AXIS));
        menuDisplayPanel.setBackground(CARD_BG);
        refreshMenuDisplay();
        
        JScrollPane mScroll = new JScrollPane(menuDisplayPanel);
        mScroll.setBorder(null);
        mScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuCard.add(mScroll, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.55; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(0, 0, 0, 10);
        main.add(menuCard, gbc);

        // 3. Checkout Section (Right Card)
        JPanel checkCard = createStyledCard();
        checkCard.setLayout(new BorderLayout(10, 10));
        
        JLabel cTitle = new JLabel("🛒 Your Checkout", SwingConstants.LEFT);
        cTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        checkCard.add(cTitle, BorderLayout.NORTH);

        // Middle Part of Card: Details
        JPanel centerDetails = new JPanel(new GridBagLayout());
        centerDetails.setBackground(CARD_BG);
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.fill = GridBagConstraints.HORIZONTAL; dgbc.weightx = 1; dgbc.gridx = 0;

        nameField = createStyledInput("Your Name...");
        phoneField = createStyledInput("Phone Number...");
        addressField = createStyledInput("Delivery Address...");
        promoField = createStyledInput("Promo Code...");
        
        dgbc.gridy = 0; dgbc.insets = new Insets(5, 0, 0, 0);
        centerDetails.add(createInputWrapper("👤 Customer Name", nameField), dgbc);
        dgbc.gridy = 1; dgbc.insets = new Insets(5, 0, 0, 0);
        centerDetails.add(createInputWrapper("📞 Phone Number", phoneField), dgbc);
        dgbc.gridy = 2; dgbc.insets = new Insets(5, 0, 0, 0);
        centerDetails.add(createInputWrapper("📍 Delivery Location", addressField), dgbc);
        dgbc.gridy = 3; dgbc.insets = new Insets(5, 0, 0, 0);
        centerDetails.add(createInputWrapper("🎟️ Discount Code", promoField), dgbc);
        
        summaryArea = new JTextArea(6, 20);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBackground(new Color(248, 249, 250));
        summaryArea.setBorder(new CompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(8,8,8,8)));
        dgbc.gridy = 4; dgbc.weighty = 1; dgbc.fill = GridBagConstraints.BOTH; dgbc.insets = new Insets(10, 0, 0, 0);
        centerDetails.add(new JScrollPane(summaryArea), dgbc);

        totalLabel = new JLabel("Total TK 0", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(ACCENT);
        dgbc.gridy = 5; dgbc.weighty = 0; dgbc.fill = GridBagConstraints.HORIZONTAL; dgbc.insets = new Insets(5, 0, 0, 0);
        centerDetails.add(totalLabel, dgbc);

        checkCard.add(centerDetails, BorderLayout.CENTER);

        // Bottom Part: PLACE ORDER Button
        JPanel footerPanel = new JPanel(new BorderLayout(5, 5));
        footerPanel.setBackground(CARD_BG);
        
        JButton placeBtn = createBtn("PLACE ORDER NOW", ACCENT);
        placeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        placeBtn.setPreferredSize(new Dimension(0, 55));
        
        JPanel extraBtns = new JPanel(new GridLayout(1, 2, 5, 5));
        extraBtns.setBackground(CARD_BG);
        JButton resetBtn = createBtn("Clear Cart", NEUTRAL);
        JButton expBtn = createBtn("Save", SECONDARY);
        extraBtns.add(resetBtn); extraBtns.add(expBtn);

        footerPanel.add(placeBtn, BorderLayout.CENTER);
        footerPanel.add(extraBtns, BorderLayout.SOUTH);
        
        checkCard.add(footerPanel, BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.45; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(0, 5, 0, 0);
        main.add(checkCard, gbc);

        // Listeners
        promoField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { calculateTotal(); }
        });
        resetBtn.addActionListener(e -> resetOrder());
        placeBtn.addActionListener(e -> confirmOrder());
        expBtn.addActionListener(e -> exportReceipt());

        return main;
    }

    private JPanel createHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(APP_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = createStyledCard();
        card.setLayout(new BorderLayout(15, 15));
        
        String[] cols = {"ID", "Date/Time", "Customer", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(tableModel);
        styleTable(historyTable);
        refreshHistoryTable();
        
        card.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bPanel.setBackground(CARD_BG);
        JButton detBtn = createBtn("View Full Receipt", SECONDARY);
        JButton updBtn = createBtn("Update Status", ACCENT);
        bPanel.add(detBtn); bPanel.add(updBtn);
        card.add(bPanel, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);

        detBtn.addActionListener(e -> {
            int r = historyTable.getSelectedRow();
            if(r != -1) JOptionPane.showMessageDialog(this, orders.get(r).getSummary(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Select an order first!", "Selection", JOptionPane.WARNING_MESSAGE);
        });

        updBtn.addActionListener(e -> {
            int row = historyTable.getSelectedRow();
            if (row != -1) {
                String[] statuses = {"Pending", "Cooking", "Out for Delivery", "Delivered", "Cancelled"};
                String newStatus = (String) JOptionPane.showInputDialog(this, "Select Status:", "Update Order",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, orders.get(row).status);
                if (newStatus != null) {
                    orders.get(row).status = newStatus;
                    saveData();
                    refreshHistoryTable();
                }
            }
        });

        return p;
    }

    private JPanel createAdminPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(APP_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = createStyledCard();
        card.setLayout(new BorderLayout(15, 15));
        
        JLabel aTitle = new JLabel("🍱 Menu Management", SwingConstants.LEFT);
        aTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        card.add(aTitle, BorderLayout.NORTH);

        DefaultTableModel mModel = new DefaultTableModel(new String[]{"Item Name", "Price (TK)"}, 0);
        JTable mTable = new JTable(mModel);
        styleTable(mTable);
        
        card.add(new JScrollPane(mTable), BorderLayout.CENTER);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        ctrl.setBackground(CARD_BG);
        JTextField nf = createStyledInput("Item Name");
        JTextField pf = createStyledInput("Price");
        JButton addB = createBtn("Save Item", SECONDARY);
        JButton delB = createBtn("Delete", NEUTRAL);
        ctrl.add(createInputWrapper("Name", nf));
        ctrl.add(createInputWrapper("Price", pf));
        ctrl.add(addB); ctrl.add(delB);
        card.add(ctrl, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);
        
        Runnable refreshAdmin = () -> {
            mModel.setRowCount(0); 
            for(MenuItem m : menuItems) mModel.addRow(new Object[]{m.name, m.price});
        };
        refreshAdmin.run();
        
        addB.addActionListener(e -> {
            try {
                String n = nf.getText().trim(); 
                int pr = Integer.parseInt(pf.getText().trim());
                if(n.isEmpty()) return;
                boolean up = false;
                for(MenuItem m : menuItems) if(m.name.equalsIgnoreCase(n)){ m.price = pr; up = true; break; }
                if(!up) menuItems.add(new MenuItem(n, pr));
                saveData(); refreshMenuDisplay(); refreshAdmin.run();
                nf.setText(""); pf.setText("");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Enter valid price!"); }
        });

        delB.addActionListener(e -> {
            int row = mTable.getSelectedRow();
            if(row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete this menu item permanently?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    menuItems.remove(row);
                    saveData(); refreshMenuDisplay(); refreshAdmin.run();
                }
            }
        });

        return p;
    }

    // --- UI HELPERS ---
    private JPanel createStyledCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(new CompoundBorder(
            new RoundedBorder(20, new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return p;
    }

    private JButton createBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // forces Java to draw the button itself, ignoring OS theme
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 25, 12, 25));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private JTextField createStyledInput(String prompt) {
        JTextField f = new JTextField(15);
        f.setFont(FONT_REG);
        f.setBorder(new CompoundBorder(new RoundedBorder(10, new Color(210, 210, 210)), new EmptyBorder(10, 15, 10, 15)));
        return f;
    }

    private JPanel createInputWrapper(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(CARD_BG);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(100, 100, 100));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) {
        t.setRowHeight(40);
        t.setFont(FONT_REG);
        t.getTableHeader().setFont(FONT_BOLD);
        t.getTableHeader().setBackground(new Color(241, 243, 245));
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(255, 240, 240));
        t.setSelectionForeground(Color.BLACK);
        t.setGridColor(new Color(233, 236, 239));
    }

    // --- LOGIC ---
    private void refreshMenuDisplay() {
        menuDisplayPanel.removeAll();
        for (MenuItem item : menuItems) {
            JPanel row = new JPanel(new BorderLayout(20, 0));
            row.setBackground(CARD_BG);
            row.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JLabel l = new JLabel(item.name + "  •  TK " + item.price);
            l.setFont(new Font("Segoe UI", Font.BOLD, 15));
            l.setForeground(NEUTRAL);
            row.add(l, BorderLayout.WEST);
            
            JSpinner s = new JSpinner(new SpinnerNumberModel(item.quantity, 0, 100, 1));
            s.setPreferredSize(new Dimension(70, 32));
            s.addChangeListener(e -> {
                item.quantity = (Integer) s.getValue();
                calculateTotal();
            });
            row.add(s, BorderLayout.EAST);
            
            row.setBorder(new MatteBorder(0, 0, 1, 0, new Color(245, 245, 245)));
            menuDisplayPanel.add(row);
        }
        menuDisplayPanel.revalidate(); menuDisplayPanel.repaint();
    }

    private int getDiscount(int subtotal) {
        String p = promoField.getText().trim().toUpperCase();
        if (p.equals("MU50")) return 50;
        if (p.equals("OFF10")) return (int) (subtotal * 0.1);
        return 0;
    }

    private void calculateTotal() {
        int sub = 0; 
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-5s %s\n", "Item", "Qty", "Price"));
        sb.append("-----------------------------\n");
        
        for(MenuItem m : menuItems) {
            if(m.quantity > 0) {
                sub += m.getSubtotal();
                sb.append(String.format("%-15s x%-4d TK %d\n", m.name, m.quantity, m.getSubtotal()));
            }
        }

        int dis = getDiscount(sub);
        
        int tot = Math.max(0, sub - dis);
        totalLabel.setText("Total TK " + tot);
        
        if(dis > 0) {
            sb.append("-----------------------------\n");
            sb.append(String.format("%-20s -TK %d\n", "Discount", dis));
        }
        sb.append("-----------------------------\n");
        sb.append(String.format("%-20s TK %d\n", "GRAND TOTAL", tot));
        
        summaryArea.setText(sb.toString());
    }

    private void resetOrder() {
        for(MenuItem m : menuItems) m.quantity = 0; 
        refreshMenuDisplay();
        nameField.setText(""); 
        phoneField.setText(""); 
        addressField.setText(""); 
        promoField.setText("");
        totalLabel.setText("Total TK 0"); 
        summaryArea.setText("");
    }

    private void confirmOrder() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String addr = addressField.getText().trim();
        
        if(name.isEmpty() || phone.isEmpty() || addr.isEmpty()){ 
            JOptionPane.showMessageDialog(this, "Please fill in all delivery details!", "Missing Information", JOptionPane.WARNING_MESSAGE); 
            return; 
        }

        if (!phone.matches("^[0-9+\\-\\s]{7,15}$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid phone number!",
                    "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean hasItems = false;
        for(MenuItem m : menuItems) if(m.quantity > 0) hasItems = true;
        if(!hasItems) { 
            JOptionPane.showMessageDialog(this, "Your cart is empty! Add some items first.", "No Items", JOptionPane.WARNING_MESSAGE); 
            return; 
        }

        calculateTotal();
        int sub = 0; for(MenuItem m : menuItems) sub += m.getSubtotal();
        int dis = getDiscount(sub);
        
        Order o = new Order(menuItems, name, phone, addr, dis);
        orders.add(o); 
        saveData(); 
        refreshHistoryTable();
        
        JOptionPane.showMessageDialog(this, "Order Placed Successfully!\n\nCustomer: " + name + "\nAddress: " + addr + "\nTotal: TK " + o.total, "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        resetOrder();
    }

    private void exportReceipt() {
        if(summaryArea.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Add items to cart first!"); return; }
        JFileChooser jfc = new JFileChooser();
        if(jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("MU FOOD CORNER ELITE - RECEIPT");
                pw.println("================================");
                pw.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                pw.println("Customer: " + nameField.getText());
                pw.println("Phone: " + phoneField.getText());
                pw.println("Address: " + addressField.getText());
                pw.println("--------------------------------");
                pw.println(summaryArea.getText());
                pw.println("================================");
                pw.println("Thank you for ordering!");
                JOptionPane.showMessageDialog(this, "Receipt saved successfully!");
            } catch(Exception e) { JOptionPane.showMessageDialog(this, "Error saving file!"); }
        }
    }

    private void refreshHistoryTable() {
        tableModel.setRowCount(0);
        for(int i=0; i<orders.size(); i++) {
            Order o = orders.get(i);
            tableModel.addRow(new Object[]{i+1, o.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")), o.name, "TK "+o.total, o.status});
        }
    }

    private void saveData() {
        try {
            ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("orders.ser"));
            oos1.writeObject(orders); oos1.close();
            ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("menu.ser"));
            oos2.writeObject(menuItems); oos2.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save data: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try { ObjectInputStream ois = new ObjectInputStream(new FileInputStream("orders.ser")); orders = (ArrayList<Order>) ois.readObject(); ois.close(); } catch(Exception e) {
            System.err.println("Could not load orders.ser: " + e.getMessage());
            orders = new ArrayList<>();
        }
        try { ObjectInputStream ois = new ObjectInputStream(new FileInputStream("menu.ser")); menuItems = (ArrayList<MenuItem>) ois.readObject(); ois.close(); } catch(Exception e) {
            menuItems = new ArrayList<>();
            menuItems.add(new MenuItem("Khichuri", 40)); menuItems.add(new MenuItem("Shingara", 10));
            menuItems.add(new MenuItem("Eggchop", 10)); menuItems.add(new MenuItem("Drinks", 25));
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(MUFoodCornerAdvanced::new); }
}
