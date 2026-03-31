import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuLoader {
    
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
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Could not read " + filename);
        }
        return menu;
    }

    public static List<Order> loadOrdersForSimulation(String filename, Map<String, MenuItem> menu) {
        // RESTORED FIX: LinkedHashMap strictly preserves chronological order of CSV
        Map<String, Order> tempOrders = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("timestamp")) continue;
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                
                String timestamp = parts[0].trim(); // RESTORED FIX: Extract timestamp
                String customerId = parts[1].trim();
                MenuItem item = menu.get(parts[2].trim());
                if (item != null) {
                    tempOrders.computeIfAbsent(customerId, k -> new Order(customerId, timestamp)).addItem(item);
                }
            }
        } catch (IOException e) {
             System.err.println("ERROR: Could not read " + filename);
        }
        return new ArrayList<>(tempOrders.values());
    }
}