import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class ManualOrderWindow extends JFrame {
    
    // Core Data
    private Map<String, MenuItem> menu;
    private OrderQueue queue;
    
    // UI & Cart State Management
    private JPanel gridPanel;
    private List<MenuItem> currentCart;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;

    public ManualOrderWindow(Map<String, MenuItem> menu, OrderQueue queue) {
        this.menu = menu;
        this.queue = queue;
        this.currentCart = new ArrayList<>();
        this.cartListModel = new DefaultListModel<>();
        
        setTitle("Point of Sale");
        setSize(950, 550); // Widened to fit the sidebar cleanly
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));
        
        // Apply Centralized Theme to Background
        getContentPane().setBackground(Theme.BG_DARK);
        
        // --- WEST PANEL: Category Sidebar ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(160, 0));
        sidebarPanel.setBackground(Theme.BG_PANEL); // Using Theme Panel Color
        sidebarPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR), "Categories", 
            TitledBorder.LEFT, TitledBorder.TOP, Theme.TITLE_FONT, Theme.TEXT_LIGHT));
        
        // Add spacing and category buttons (using Theme colors)
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(createCategoryButton("All Items", null, new Color(150, 150, 150)));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(createCategoryButton("Beverages", Category.BEVERAGE, Theme.BTN_BEVERAGE));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(createCategoryButton("Food", Category.FOOD, Theme.BTN_FOOD));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(createCategoryButton("Other", Category.OTHER, Theme.BTN_OTHER));
        
        add(sidebarPanel, BorderLayout.WEST);

        // --- CENTER PANEL: Dynamic Menu Grid ---
        gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        gridPanel.setBackground(Theme.BG_DARK);
        
        JScrollPane menuScroll = new JScrollPane(gridPanel);
        menuScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR), "Menu (Click to add)", 
            TitledBorder.LEFT, TitledBorder.TOP, Theme.TITLE_FONT, Theme.TEXT_LIGHT));
        add(menuScroll, BorderLayout.CENTER);
        
        // Populate the grid initially with all items
        filterMenu(null);
        
        // --- EAST PANEL: Interactive Cart ---
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setPreferredSize(new Dimension(280, 0));
        cartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        cartPanel.setBackground(Theme.BG_DARK);
        
        cartList = new JList<>(cartListModel);
        cartList.setFont(Theme.RECEIPT_FONT);
        cartList.setBackground(Theme.RECEIPT_BG);
        cartList.setForeground(Theme.RECEIPT_TEXT);
        
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR), "Current Cart", 
            TitledBorder.LEFT, TitledBorder.TOP, Theme.TITLE_FONT, Theme.TEXT_LIGHT));
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        // Cart Controls
        JPanel cartControls = new JPanel(new GridLayout(5, 1, 5, 8)); 
        cartControls.setOpaque(false);
        
        totalLabel = new JLabel("Total: £0.00", SwingConstants.RIGHT);
        totalLabel.setFont(Theme.HEADER_FONT);
        totalLabel.setForeground(Theme.TEXT_LIGHT);
        cartControls.add(totalLabel);
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeBtn.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex != -1) {
                currentCart.remove(selectedIndex);
                updateCartUI();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        });
        cartControls.add(removeBtn);
        
        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        clearBtn.addActionListener(e -> {
            currentCart.clear();
            updateCartUI();
        });
        cartControls.add(clearBtn);
        
        JButton sendBtn = new JButton("Send Order to Baristas");
        sendBtn.setBackground(Theme.CHECKOUT_BTN);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(Theme.BUTTON_FONT);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.setUI(new BasicButtonUI());
        
        sendBtn.addActionListener(e -> {
            if (currentCart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // FIX: Generate Stage 1 Timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            // FIX: Generate Random Ticket ID to prevent log collisions
            int ticketNumber = (int) (Math.random() * 9000) + 1000;
            Order liveOrder = new Order("WALK-IN-" + ticketNumber, timestamp);
            
            for (MenuItem item : currentCart) {
                liveOrder.addItem(item);
            }
            
            queue.addOrder(liveOrder); 
            
            // FIX: Background thread to prevent UI freezing on I/O
            new Thread(() -> saveOrderToCSV(liveOrder)).start(); 
            
            // Note: respected teammate's choice to remove success popup for snappier ordering!
            currentCart.clear();
            updateCartUI();
        });
        cartControls.add(sendBtn);
        
        JButton closeShopBtn = new JButton("Close Shop (Stop Simulation)");
        closeShopBtn.setBackground(Theme.CLOSE_BTN);
        closeShopBtn.setForeground(Theme.TEXT_LIGHT);
        closeShopBtn.setFont(Theme.BUTTON_FONT);
        closeShopBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeShopBtn.setUI(new BasicButtonUI());
        
        closeShopBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to close the shop?\nBaristas will finish the remaining queue and shut down.", 
                "Close Shop", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                queue.setGenerationFinished(); 
                this.dispose(); 
            }
        });
        cartControls.add(closeShopBtn);
        
        cartPanel.add(cartControls, BorderLayout.SOUTH);
        add(cartPanel, BorderLayout.EAST);
    }
    
    // --- UI HELPER METHODS ---

    // Creates the buttons for the Sidebar
    private JButton createCategoryButton(String text, Category filterCategory, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(140, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new BasicButtonUI()); // Removes default Swing styling
        
        // When clicked, rebuild the Center grid!
        btn.addActionListener(e -> filterMenu(filterCategory));
        return btn;
    }

    // Clears and repopulates the Center Grid based on the chosen category
    private void filterMenu(Category filter) {
        gridPanel.removeAll(); // Clear existing buttons
        
        for (MenuItem item : menu.values()) {
            // If filter is null, show everything. Otherwise, only show matches.
            if (filter == null || item.getCategory() == filter) {
                
                String btnText = "<html><div style='text-align:center;'>"
                               + "<span style='font-size:14px; font-weight:bold; color:" + Theme.BTN_TEXT_HEX + "; font-family:sans-serif;'>" + item.getName() + "</span><br><br>"
                               + "<span style='font-size:16px; color:#c0392b; font-family:sans-serif;'><b>£" + String.format("%.2f", item.getCost()) + "</b></span>"
                               + "</div></html>";
                               
                JButton btn = new JButton(btnText);
                btn.setUI(new BasicButtonUI());
                
                // Color code based on category from Theme.java
                if (item.getCategory() == Category.BEVERAGE) btn.setBackground(Theme.BTN_BEVERAGE);
                else if (item.getCategory() == Category.FOOD) btn.setBackground(Theme.BTN_FOOD);
                else btn.setBackground(Theme.BTN_OTHER);
                
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                btn.addActionListener(e -> {
                    currentCart.add(item);
                    updateCartUI();
                });
                
                gridPanel.add(btn);
            }
        }
        
        // Force Swing to redraw the UI with the new buttons
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    // Keeps the JList and Total Label synchronized with the Cart array
    private void updateCartUI() {
        cartListModel.clear();
        double rawTotal = 0;
        
        Order tempOrder = new Order("TEMP", "N/A"); // Updated to use the correct constructor
        for (MenuItem item : currentCart) {
            cartListModel.addElement(String.format("%-18s £%.2f", item.getName(), item.getCost()));
            rawTotal += item.getCost();
            tempOrder.addItem(item); 
        }
        
        double finalTotal = DiscountEngine.calculateFinalBill(tempOrder);
        
        if (finalTotal < rawTotal) {
            totalLabel.setText(String.format("Total: £%.2f (20%% OFF!)", finalTotal));
            totalLabel.setForeground(Theme.TOTAL_GREEN); 
        } else {
            totalLabel.setText(String.format("Total: £%.2f", finalTotal));
            totalLabel.setForeground(Theme.TEXT_LIGHT); 
        }
    }

    // Appends live orders to the history CSV
    private void saveOrderToCSV(Order order) {
        // FIX: Grab the timestamp directly from the Order object to satisfy requirements
        try (PrintWriter out = new PrintWriter(new FileWriter("orders.csv", true))) {
            for (MenuItem item : order.getItems()) {
                out.println(order.getTimestamp() + "," + order.getCustomerId() + "," + item.getId());
            }
        } catch (IOException ex) {
            System.err.println("Error saving live order to file: " + ex.getMessage());
        }
    }
}