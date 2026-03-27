package Stage1_Archive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoffeeShopTests {

    // ==========================================
    // 1. DISCOUNT ENGINE TESTS (Testing all paths)
    // ==========================================

    @Test
    public void testDiscountApplied_MealDealConditionsMet() throws InvalidIdentifierException {
        // Path 1: Order has 1 Beverage and 2 Foods -> Should get 20% off
        Order order = new Order("TEST-01");
        order.addItem(new MenuItem("BEV-001", "Coffee", 2.50, Category.BEVERAGE));
        order.addItem(new MenuItem("FOOD-001", "Sandwich", 5.00, Category.FOOD));
        order.addItem(new MenuItem("FOOD-002", "Cake", 2.50, Category.FOOD));
        
        // Raw total = £10.00. With 20% discount, final bill should be £8.00
        double expectedTotal = 8.00;
        double actualTotal = DiscountEngine.calculateFinalBill(order);
        
        assertEquals(expectedTotal, actualTotal, 0.001, "20% discount should be applied for 1 Bev and 2 Food items.");
    }

    @Test
    public void testDiscountNotApplied_MissingFood() throws InvalidIdentifierException {
        // Path 2: Order has 1 Beverage but only 1 Food -> No discount
        Order order = new Order("TEST-02");
        order.addItem(new MenuItem("BEV-001", "Coffee", 2.50, Category.BEVERAGE));
        order.addItem(new MenuItem("FOOD-001", "Sandwich", 5.00, Category.FOOD));
        
        // Raw total = £7.50. No discount, final bill should be £7.50
        double expectedTotal = 7.50;
        double actualTotal = DiscountEngine.calculateFinalBill(order);
        
        assertEquals(expectedTotal, actualTotal, 0.001, "Discount should NOT apply with only 1 food item.");
    }

    @Test
    public void testDiscountNotApplied_MissingBeverage() throws InvalidIdentifierException {
        // Path 3: Order has 3 Foods but 0 Beverages -> No discount
        Order order = new Order("TEST-03");
        order.addItem(new MenuItem("FOOD-001", "Sandwich", 5.00, Category.FOOD));
        order.addItem(new MenuItem("FOOD-002", "Cake", 2.50, Category.FOOD));
        order.addItem(new MenuItem("FOOD-003", "Wrap", 4.00, Category.FOOD));
        
        // Raw total = £11.50. No discount, final bill should be £11.50
        double expectedTotal = 11.50;
        double actualTotal = DiscountEngine.calculateFinalBill(order);
        
        assertEquals(expectedTotal, actualTotal, 0.001, "Discount should NOT apply without a beverage.");
    }

    // ==========================================
    // 2. EXCEPTION HANDLING TESTS
    // ==========================================

    @Test
    public void testExceptionThrownOnInvalidId() {
        // Expect an InvalidIdentifierException when the ID format is wrong
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            new MenuItem("BAD-ID-123", "Corrupted Item", 0.00, Category.OTHER);
        });

        // Verify the exception was thrown and has a message (Flexible check so it matches any version of MenuItem.java)
        assertNotNull(exception.getMessage(), "Exception message should not be null.");
        assertTrue(exception.getMessage().toLowerCase().contains("pattern") || exception.getMessage().toLowerCase().contains("id"), 
            "Exception message should explain the pattern failure.");
    }

    @Test
    public void testNoExceptionOnValidId() {
        // Prove that a valid ID creates the object successfully without throwing errors
        assertDoesNotThrow(() -> {
            MenuItem item = new MenuItem("BEV-999", "Valid Drink", 1.50, Category.BEVERAGE);
            assertEquals("BEV-999", item.getId());
        });
    }
}