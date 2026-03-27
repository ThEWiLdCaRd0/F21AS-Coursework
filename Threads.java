import java.util.ArrayList;
import java.util.List;

// 1. PRODUCER THREAD: Gradually adds customers to the queue
class CustomerGenerator implements Runnable {
    private OrderQueue queue;
    private List<Order> historicalOrders;

    public CustomerGenerator(OrderQueue queue, List<Order> historicalOrders) {
        this.queue = queue;
        this.historicalOrders = historicalOrders;
    }

    @Override
    public void run() {
        try {
            for (Order order : historicalOrders) {
                // Wait 1-3 seconds before next customer arrives (scaled by speed)
                long delay = (long) (1000 + Math.random() * 2000);
                Thread.sleep(delay / SimulationController.getSpeedMultiplier());
                queue.addOrder(order);
            }
            Logger.getInstance().logEvent("The doors are closed. No more customers arriving.");
            //queue.setGenerationFinished();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// 2. CONSUMER THREAD: The Barista/Serving Staff
class Server extends Thread implements Subject {
    private String serverName;
    private OrderQueue queue;
    private String currentStatus;
    private List<ObserverInterfaces> observers = new ArrayList<>();

    public Server(String name, OrderQueue queue) {
        this.serverName = name;
        this.queue = queue;
        setStatus("Idle - Waiting for customers");
    }

    public String getServerName() { return serverName; }
    public String getCurrentStatus() { return currentStatus; }

    private void setStatus(String status) {
        this.currentStatus = status;
        notifyObservers();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = queue.fetchNextOrder();
                if (order == null) break; // Queue empty and doors closed

                Logger.getInstance().logEvent(serverName + " started processing order for " + order.getCustomerId());
                
                // Build status string
                StringBuilder sb = new StringBuilder();
                sb.append("Processing ").append(order.getCustomerId()).append("...\n");
                long totalPrepTime = 0;
                
                for (MenuItem item : order.getItems()) {
                    sb.append("- ").append(item.getName()).append("\n");
                    // Requirement: Beverages 2-4s, Food 6-10s
                    if (item.getCategory() == Category.BEVERAGE) {
                        totalPrepTime += 2000 + (Math.random() * 2000); 
                    } else if (item.getCategory() == Category.FOOD) {
                        totalPrepTime += 6000 + (Math.random() * 4000);
                    } else {
                        totalPrepTime += 1500;
                    }
                }
                
                double finalTotal = DiscountEngine.calculateFinalBill(order);
                sb.append("\nTotal: £").append(String.format("%.2f", finalTotal));
                setStatus(sb.toString());

                // Simulate the actual making of the coffee
                Thread.sleep(totalPrepTime / SimulationController.getSpeedMultiplier());
                
                Logger.getInstance().logEvent(serverName + " finished order for " + order.getCustomerId());
                setStatus("Idle - Waiting for customers");
            }
            setStatus("Finished Shift - Going Home");
            Logger.getInstance().logEvent(serverName + " clocked out.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void registerObserver(ObserverInterfaces o) { observers.add(o); }
    @Override
    public void removeObserver(ObserverInterfaces o) { observers.remove(o); }
    @Override
    public void notifyObservers() {
        for (ObserverInterfaces o : observers) o.update();
    }
}