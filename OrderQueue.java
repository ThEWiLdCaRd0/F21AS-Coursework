import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// MULTI-THREADING: This is the shared resource safely accessed by all threads
public class OrderQueue implements Subject {
    private Queue<Order> queue = new LinkedList<>();
    private List<ObserverInterfaces> observers = new ArrayList<>();
    private boolean generationFinished = false;

    // Synchronized to prevent thread interference (Mandatory for Stage 2)
    public synchronized void addOrder(Order order) {
        queue.add(order);
        Logger.getInstance().logEvent(order.getCustomerId() + " joined the queue with " + order.getItems().size() + " items.");
        notifyObservers();
        notifyAll(); // Wake up any waiting servers
    }

    // Servers call this. It blocks if empty.
    public synchronized Order fetchNextOrder() throws InterruptedException {
        while (queue.isEmpty() && !generationFinished) {
            wait(); // Server waits here until an order is added
        }
        if (queue.isEmpty() && generationFinished) {
            return null; // Signals the server to go home
        }
        Order order = queue.poll();
        notifyObservers();
        return order;
    }

    public synchronized void setGenerationFinished() {
        this.generationFinished = true;
        notifyAll(); // Wake up servers so they can see the queue is permanently empty and exit
    }

    public synchronized List<Order> getQueueSnapshot() {
        return new ArrayList<>(queue);
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