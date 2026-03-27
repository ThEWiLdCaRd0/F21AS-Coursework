package Stage1_Archive;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Secondary class: No 'public' keyword
class InvalidIdentifierException extends Exception {
    public InvalidIdentifierException(String message) {
        super(message);
    }
}

// Secondary class: No 'public' keyword
class MenuList {
    private Map<String, MenuItem> menuMap = new HashMap<>();
    public void addItem(MenuItem item) { menuMap.put(item.getId(), item); }
    public MenuItem getItem(String id) { return menuMap.get(id); }
    public Map<String, MenuItem> getAllItems() { return menuMap; }
}

// Main class: MUST be public
public class MenuLoader {
    public static MenuList loadMenu(String filename) {
        MenuList menu = new MenuList();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("id")) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                try {
                    MenuItem item = new MenuItem(parts[0].trim(), parts[1].trim(), 
                                      Double.parseDouble(parts[2].trim()), 
                                      Category.valueOf(parts[3].trim().toUpperCase()));
                    menu.addItem(item);
                } catch (InvalidIdentifierException e) {
                    System.err.println("SKIPPED LINE: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Could not read " + filename);
        }
        return menu;
    }

    public static void loadOrders(String filename, MenuList menuList, OrderManager orderManager) {
        Map<String, Order> tempOrders = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("timestamp")) continue;
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                MenuItem item = menuList.getItem(parts[2].trim());
                if (item != null) {
                    String customerId = parts[1].trim();
                    Order order = tempOrders.getOrDefault(customerId, new Order(customerId));
                    order.addItem(item);
                    tempOrders.put(customerId, order);
                }
            }
            for (Order order : tempOrders.values()) {
                orderManager.addOrder(order);
            }
        } catch (IOException e) {
             System.err.println("ERROR: Could not read " + filename);
        }
    }
}