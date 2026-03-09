import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CoffeeShopGUI extends JFrame {
    
    private MenuList menuList; // Brought back to use for filtering
    private OrderManager orderManager;
    private Order currentOrder;
    private int customerCounter = 100;

    private JPanel menuGridPanel; // Extracted so we can refresh it
    private DefaultListModel<String> cartModel;
    private JLabel totalLabel;

    public CoffeeShopGUI(MenuList menuList, OrderManager orderManager) {
        this.menuList = menuList;
        this.orderManager = orderManager;
        this.currentOrder = new Order("CUST-" + customerCounter);

        setTitle("Stage 1 - Digital Cash Register");
        setSize(1050, 650); // Made slightly wider to accommodate the sidebar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); 

        // Main Container 
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainContainer.setBackground(Theme.BG_DARK); 

        // --- TOP HEADER ---
        JLabel headerLabel = new JLabel("☕ Coffee Shop POS System", SwingConstants.CENTER);
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_LIGHT);
        mainContainer.add(headerLabel, BorderLayout.NORTH);

        // --- LEFT AREA: SIDEBAR & MENU GRID ---
        JPanel centerWrapper = new JPanel(new BorderLayout(20, 0));
        centerWrapper.setOpaque(false);

        // 1. Sidebar (Categories)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(160, 0));

        JLabel catLabel = new JLabel("Categories");
        catLabel.setFont(Theme.TITLE_FONT);
        catLabel.setForeground(Theme.TEXT_LIGHT);
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(catLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sidebar Filter Buttons
        sidebarPanel.add(createSidebarButton("All Items", null));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(createSidebarButton("Beverages", Category.BEVERAGE));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(createSidebarButton("Food", Category.FOOD));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(createSidebarButton("Other / Addons", Category.OTHER));

        // 2. Menu Grid
        menuGridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); 
        menuGridPanel.setOpaque(false);
        
        JScrollPane menuScroll = new JScrollPane(menuGridPanel);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 2),
                "Menu Items",
                TitledBorder.LEFT, TitledBorder.TOP,
                Theme.TITLE_FONT,
                Theme.TEXT_LIGHT
        ));

        // Load all items initially
        populateMenuGrid(null);

        centerWrapper.add(sidebarPanel, BorderLayout.WEST);
        centerWrapper.add(menuScroll, BorderLayout.CENTER);

        // --- RIGHT PANEL: CURRENT ORDER ---
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BorderLayout(10, 15)); 
        cartPanel.setOpaque(false);
        cartPanel.setPreferredSize(new Dimension(320, 0)); 
        cartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 2),
                "Current Order",
                TitledBorder.LEFT, TitledBorder.TOP,
                Theme.TITLE_FONT,
                Theme.TEXT_LIGHT
        ));

        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        cartList.setFont(Theme.RECEIPT_FONT);
        cartList.setBackground(Theme.RECEIPT_BG); 
        cartList.setForeground(Theme.RECEIPT_TEXT);
        cartList.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        // Bottom Right Panel
        JPanel bottomCartPanel = new JPanel(new GridLayout(3, 1, 0, 10)); 
        bottomCartPanel.setOpaque(false);
        
        totalLabel = new JLabel("Total: £0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalLabel.setForeground(Theme.TOTAL_RED); 
        
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(Theme.BUTTON_FONT);
        checkoutBtn.setUI(new BasicButtonUI()); 
        checkoutBtn.setBackground(Theme.CHECKOUT_BTN); 
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setOpaque(true);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 120, 65), 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        checkoutBtn.addActionListener(e -> checkout());

        JButton closeBtn = new JButton("Close Shop & Generate Report");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeBtn.setUI(new BasicButtonUI()); 
        closeBtn.setBackground(Theme.CLOSE_BTN); 
        closeBtn.setForeground(Theme.TEXT_LIGHT);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 40), 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        closeBtn.addActionListener(e -> closeShop());

        bottomCartPanel.add(totalLabel);
        bottomCartPanel.add(checkoutBtn);
        bottomCartPanel.add(closeBtn);
        cartPanel.add(bottomCartPanel, BorderLayout.SOUTH);

        // Final Assembly
        mainContainer.add(centerWrapper, BorderLayout.CENTER);
        mainContainer.add(cartPanel, BorderLayout.EAST);
        setContentPane(mainContainer);
    }

    // --- HELPER METHODS ---

    // Creates standardized buttons for the sidebar
    private JButton createSidebarButton(String text, Category filterCategory) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setUI(new BasicButtonUI());
        btn.setBackground(Theme.BORDER_COLOR);
        btn.setForeground(Theme.TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(150, 40));
        
        // Add click listener to filter the menu
        btn.addActionListener(e -> populateMenuGrid(filterCategory));
        
        return btn;
    }

    // Clears and refills the center grid based on the selected category
    private void populateMenuGrid(Category filter) {
        menuGridPanel.removeAll(); // Clear existing buttons

        for (MenuItem item : menuList.getAllItems().values()) {
            // Skip items that don't match the filter (if a filter is applied)
            if (filter != null && item.getCategory() != filter) {
                continue;
            }

            Color bgColor;
            Color borderColor;
            if (item.getCategory() == Category.BEVERAGE) {
                bgColor = Theme.BTN_BEVERAGE;
                borderColor = new Color(70, 150, 220);
            } else if (item.getCategory() == Category.FOOD) {
                bgColor = Theme.BTN_FOOD;
                borderColor = new Color(100, 200, 100);
            } else {
                bgColor = Theme.BTN_OTHER;
                borderColor = new Color(220, 170, 70);
            }

            String btnText = "<html><div style='text-align:center;'>"
                           + "<span style='font-size:14px; font-weight:bold; color:" + Theme.BTN_TEXT_HEX + "; font-family:sans-serif;'>" + item.getName() + "</span><br><br>"
                           + "<span style='font-size:16px; color:#c0392b; font-family:sans-serif;'><b>£" + String.format("%.2f", item.getCost()) + "</b></span>"
                           + "</div></html>";
            
            JButton btn = new JButton(btnText);
            btn.setPreferredSize(new Dimension(130, 90));
            btn.setUI(new BasicButtonUI()); 
            btn.setBackground(bgColor);
            btn.setOpaque(true);
            btn.setFocusPainted(false); 
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            btn.addActionListener(e -> {
                currentOrder.addItem(item);
                updateCartUI();
            });
            menuGridPanel.add(btn);
        }

        // Force the GUI to refresh the layout and repaint the screen
        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private void updateCartUI() {
        cartModel.clear();
        for (MenuItem item : currentOrder.getItems()) {
            cartModel.addElement(String.format("%-20s £%.2f", item.getName(), item.getCost()));
        }
        double raw = currentOrder.getRawTotal();
        double finalTotal = DiscountEngine.calculateFinalBill(currentOrder);
        
        if (finalTotal < raw) {
            totalLabel.setText(String.format("Total: £%.2f (20%% Deal!)", finalTotal));
            totalLabel.setForeground(Theme.TOTAL_GREEN); 
        } else {
            totalLabel.setText(String.format("Total: £%.2f", finalTotal));
            totalLabel.setForeground(Theme.TOTAL_RED); 
        }
    }

    private void saveOrderToFile(Order order) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        // The 'true' parameter in FileWriter enables append mode
        try (PrintWriter out = new PrintWriter(new FileWriter("orders.csv", true))) {
            for (MenuItem item : order.getItems()) {
                out.println(timestamp + "," + order.getCustomerId() + "," + item.getId());
            }
        } catch (IOException e) {
            System.err.println("Error saving order to file: " + e.getMessage());
        }
    }

    private void checkout() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        orderManager.addOrder(currentOrder);
        saveOrderToFile(currentOrder); // Save the order items directly to the CSV file
        
        JOptionPane.showMessageDialog(this, 
            "Order successful!\nTotal Paid: £" + String.format("%.2f", DiscountEngine.calculateFinalBill(currentOrder)), 
            "Checkout Complete", JOptionPane.INFORMATION_MESSAGE);
        
        customerCounter++;
        currentOrder = new Order("CUST-" + customerCounter);
        updateCartUI();
    }

    private void closeShop() {
        String report = orderManager.generateSummaryReport();
        JTextArea reportArea = new JTextArea(report);
        reportArea.setFont(Theme.RECEIPT_FONT);
        reportArea.setEditable(false);
        reportArea.setBackground(Theme.TEXT_LIGHT);
        reportArea.setForeground(Color.BLACK);
        
        JOptionPane.showMessageDialog(this, new JScrollPane(reportArea), "End of Day Report", JOptionPane.INFORMATION_MESSAGE);
        System.out.println(report);
        System.exit(0);
    }

    // --- MAIN ENTRY POINT --- //
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set System Look and Feel.");
        }

        MenuList loadedMenu = MenuLoader.loadMenu("menu.csv");
        OrderManager manager = new OrderManager();
        MenuLoader.loadOrders("orders.csv", loadedMenu, manager);

        SwingUtilities.invokeLater(() -> {
            CoffeeShopGUI gui = new CoffeeShopGUI(loadedMenu, manager);
            gui.setVisible(true);
        });
    }
}