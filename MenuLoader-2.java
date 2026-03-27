import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuLoader {
    
    // Loads the menu catalog into a fast HashMap
    public static Map<String, MenuItem> loadMenu(String filename) {
        Map<String, MenuItem> menu = new HashMap<>();
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
                    menu.put(item.getId(), item);
                } catch (InvalidIdentifierException ignored) {
                    // Stage 2: Silently ignore bad lines to keep simulation booting clean
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Could not read " + filename);
        }
        return menu;
    }

    // Stage 2: Returns a list of orders to be gradually added by the Producer Thread
    public static List<Order> loadOrdersForSimulation(String filename, Map<String, MenuItem> menu) {
        Map<String, Order> tempOrders = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("timestamp")) continue;
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                
                String customerId = parts[1].trim();
                MenuItem item = menu.get(parts[2].trim());
                if (item != null) {
                    tempOrders.computeIfAbsent(customerId, k -> new Order(customerId)).addItem(item);
                }
            }
        } catch (IOException e) {
             System.err.println("ERROR: Could not read " + filename);
        }
        // Return as a List so the Generator Thread can loop through them sequentially
        return new ArrayList<>(tempOrders.values());
    }
}