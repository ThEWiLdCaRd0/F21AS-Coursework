import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationTests {

    @Test
    public void testLoggerIsSingleton() {
        // Fetch the logger instance twice
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();

        // assertSame checks if they point to the EXACT same object in memory
        assertSame(logger1, logger2, "Logger should follow the Singleton pattern and only create one instance.");
    }

    @Test
    public void testOrderQueueAddsOrders() {
        OrderQueue queue = new OrderQueue();
        // FIX: Added a dummy timestamp to satisfy the updated Order constructor requirement
        Order order = new Order("TEST-CUST-01", "2026-03-31 10:00");
        
        queue.addOrder(order);
        
        assertEquals(1, queue.getQueueSnapshot().size(), "Queue snapshot should contain exactly 1 order.");
        assertEquals("TEST-CUST-01", queue.getQueueSnapshot().get(0).getCustomerId(), "Customer ID should match the added order.");
    }

    @Test
    public void testOrderQueueFetchesOrders() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        // FIX: Added a dummy timestamp
        Order order = new Order("TEST-CUST-02", "2026-03-31 10:00");
        
        queue.addOrder(order); // Add first so the fetch doesn't block
        Order fetchedOrder = queue.fetchNextOrder();
        
        assertNotNull(fetchedOrder, "Fetched order should not be null.");
        assertEquals("TEST-CUST-02", fetchedOrder.getCustomerId(), "Should fetch the exact order we just added.");
        assertEquals(0, queue.getQueueSnapshot().size(), "Queue should be empty after fetching.");
    }

    @Test
    public void testOrderQueueSignalsShutdown() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        
        // Signal that the doors are closed and no more orders are coming
        queue.setGenerationFinished(); 
        
        // Fetching from an empty queue that has been flagged as finished should instantly return null 
        // (This is what tells the Barista threads to safely shut down)
        Order fetchedOrder = queue.fetchNextOrder();
        assertNull(fetchedOrder, "Fetch should return null when generation is finished and queue is empty.");
    }

    @Test
    public void testDiscountEngineMealDeal() throws InvalidIdentifierException {
        // Validating the Stage 1 business logic still works perfectly in the Stage 2 Models.java
        // FIX: Added a dummy timestamp
        Order order = new Order("TEST-CUST-03", "2026-03-31 10:00");
        order.addItem(new MenuItem("BEV-001", "Coffee", 2.50, Category.BEVERAGE));
        order.addItem(new MenuItem("FOOD-001", "Sandwich", 5.00, Category.FOOD));
        order.addItem(new MenuItem("FOOD-002", "Cake", 2.50, Category.FOOD));
        
        // Raw = 10.00, Meal Deal (20% off) = 8.00
        double expectedFinal = 8.00;
        double actualFinal = DiscountEngine.calculateFinalBill(order);
        
        assertEquals(expectedFinal, actualFinal, 0.001, "Discount Engine should apply 20% off for 1 Bev and 2 Foods.");
    }

    @Test
    public void testMenuItemThrowsExceptionOnBadID() {
        // Validating that Stage 1 custom exceptions still work
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            new MenuItem("BAD123", "Corrupted Drink", 0.00, Category.OTHER);
        });
        
        assertTrue(exception.getMessage().contains("Data Error"), "Exception should throw a descriptive data error.");
    }
}