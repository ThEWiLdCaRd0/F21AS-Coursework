import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManualOrderWindow extends JFrame {
    
    private static int liveCustomerCounter = 1;
    
    // Cart State Management
    private List<MenuItem> currentCart;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;

    public ManualOrderWindow(Map<String, MenuItem> menu, OrderQueue queue) {
        currentCart = new ArrayList<>();
        cartListModel = new DefaultListModel<>();
        
        setTitle("Live Point of Sale (Advanced)");
        setSize(750, 500); // Widened to fit the cart panel
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(40, 44, 52));
        
        // --- LEFT PANEL: Menu Grid ---
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        gridPanel.setBackground(new Color(30, 33, 39));
        
        // Generate buttons for every menu item
        for (MenuItem item : menu.values()) {
            JButton btn = new JButton("<html><center><b>" + item.getName() + "</b><br>£" + String.format("%.2f", item.getCost()) + "</center></html>");
            
            // Color code based on category
            if (item.getCategory() == Category.BEVERAGE) btn.setBackground(new Color(70, 150, 220));
            else if (item.getCategory() == Category.FOOD) btn.setBackground(new Color(100, 200, 100));
            else btn.setBackground(new Color(220, 170, 70));
            
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Action: Add to Cart (Not immediately to the queue)
            btn.addActionListener(e -> {
                currentCart.add(item);
                updateCartUI();
            });
            
            gridPanel.add(btn);
        }
        
        JScrollPane menuScroll = new JScrollPane(gridPanel);
        menuScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Menu (Click to add)", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14), Color.WHITE));
        add(menuScroll, BorderLayout.CENTER);
        
        // --- RIGHT PANEL: Interactive Cart ---
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setPreferredSize(new Dimension(280, 0));
        cartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        cartPanel.setBackground(new Color(40, 44, 52));
        
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Current Cart", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14), Color.WHITE));
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        // Cart Controls
        JPanel cartControls = new JPanel(new GridLayout(5, 1, 5, 8)); 
        cartControls.setOpaque(false);
        
        totalLabel = new JLabel("Total: £0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(Color.WHITE);
        cartControls.add(totalLabel);
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        clearBtn.addActionListener(e -> {
            currentCart.clear();
            updateCartUI();
        });
        cartControls.add(clearBtn);
        
        JButton sendBtn = new JButton("Send Order to Baristas");
        sendBtn.setBackground(new Color(39, 174, 96));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Action: Package the cart into a real Order and inject it into the thread queue
        sendBtn.addActionListener(e -> {
            if (currentCart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Order liveOrder = new Order("LIVE-USER-" + liveCustomerCounter++);
            for (MenuItem item : currentCart) {
                liveOrder.addItem(item);
            }
            
            queue.addOrder(liveOrder); // Thread-safe injection!
            saveOrderToCSV(liveOrder); // Save to orders.csv permanently
            
            JOptionPane.showMessageDialog(this, "Order successfully sent to Baristas!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset for the next customer
            currentCart.clear();
            updateCartUI();
        });
        cartControls.add(sendBtn);
        
        JButton closeShopBtn = new JButton("Close Shop (Stop Simulation)");
        closeShopBtn.setBackground(new Color(192, 57, 43));
        closeShopBtn.setForeground(Color.WHITE);
        closeShopBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeShopBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Action: Manually trigger shutdown
        closeShopBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to close the shop?\nBaristas will finish the remaining queue and shut down.", 
                "Close Shop", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                queue.setGenerationFinished(); // Manually trigger shutdown sequence
                this.dispose(); // Close this window
            }
        });
        cartControls.add(closeShopBtn);
        
        cartPanel.add(cartControls, BorderLayout.SOUTH);
        add(cartPanel, BorderLayout.EAST);
    }
    
    // Helper method to keep the JList and Total Label synchronized with the underlying ArrayList
    private void updateCartUI() {
        cartListModel.clear();
        double rawTotal = 0;
        
        // Create a temporary order just to run it through our DiscountEngine
        Order tempOrder = new Order("TEMP");
        
        for (MenuItem item : currentCart) {
            cartListModel.addElement(String.format("%-18s £%.2f", item.getName(), item.getCost()));
            rawTotal += item.getCost();
            tempOrder.addItem(item); // Add to temp order for calculation
        }
        
        // Calculate the final price using the engine
        double finalTotal = DiscountEngine.calculateFinalBill(tempOrder);
        
        // If a discount was applied, update the label visually to reflect the deal
        if (finalTotal < rawTotal) {
            totalLabel.setText(String.format("Total: £%.2f (20%% OFF!)", finalTotal));
            totalLabel.setForeground(new Color(80, 220, 120)); // Highlight in Green
        } else {
            totalLabel.setText(String.format("Total: £%.2f", finalTotal));
            totalLabel.setForeground(Color.WHITE); // Default white
        }
    }

    // NEW: File I/O method to save live orders to the history CSV
    private void saveOrderToCSV(Order order) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        // FileWriter set to 'true' appends to the end of the file rather than overwriting it
        try (PrintWriter out = new PrintWriter(new FileWriter("orders.csv", true))) {
            for (MenuItem item : order.getItems()) {
                out.println(timestamp + "," + order.getCustomerId() + "," + item.getId());
            }
        } catch (IOException ex) {
            System.err.println("Error saving live order to file: " + ex.getMessage());
        }
    }
}