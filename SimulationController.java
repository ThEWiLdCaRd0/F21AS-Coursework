import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// MVC CONTROLLER: Wires everything together and manages application lifecycle
public class SimulationController {
    
    private static int speedMultiplier = 1;

    public static synchronized int getSpeedMultiplier() {
        return speedMultiplier;
    }

    public static synchronized void setSpeedMultiplier(int speed) {
        speedMultiplier = speed;
    }

    public static void main(String[] args) {
        Logger.getInstance().logEvent("System Booting Up...");

        // 1. Load Data
        Map<String, MenuItem> menu = MenuLoader.loadMenu("menu.csv");
        List<Order> historicalOrders = MenuLoader.loadOrdersForSimulation("orders.csv", menu);
        
        // 2. Initialize Models
        OrderQueue queue = new OrderQueue();
        List<Server> servers = new ArrayList<>();
        servers.add(new Server("Barista 1 (Alice)", queue));
        servers.add(new Server("Barista 2 (Bob)", queue));

        // 3. Initialize View (GUIs)
        SwingUtilities.invokeLater(() -> {
            // Main Dashboard
            SimulationGUI gui = new SimulationGUI(queue, servers);
            gui.setLocation(100, 100);
            gui.setVisible(true);
            
            // Force an initial UI update
            gui.update();

            // Live Interactive Window (NEW EXTENSION)
            ManualOrderWindow manualInput = new ManualOrderWindow(menu, queue);
            manualInput.setLocation(1020, 100); // Pop up right next to the main dashboard
            manualInput.setVisible(true);
        });

        // 4. Start Threads
        Logger.getInstance().logEvent("Starting Simulation...");
        Thread generatorThread = new Thread(new CustomerGenerator(queue, historicalOrders));
        generatorThread.start();

        for (Server server : servers) {
            server.start();
        }

        // 5. Shutdown Hook: Wait for everything to finish, then generate report
        new Thread(() -> {
            try {
                // Wait for all servers to finish processing
                for (Server server : servers) {
                    server.join(); 
                }
                
                // Simulation is over
                Logger.getInstance().logEvent("All orders processed. Closing shop.");
                Logger.getInstance().writeLogToFile();
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, 
                        "Simulation Complete!\nCheck 'simulation_log.txt' for the final log.", 
                        "End of Day", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}