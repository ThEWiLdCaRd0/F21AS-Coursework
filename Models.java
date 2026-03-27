import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum Category { FOOD, BEVERAGE, OTHER }

class InvalidIdentifierException extends Exception {
    public InvalidIdentifierException(String message) { super(message); }
}

class MenuItem {
    private String id;
    private String name;
    private double cost;
    private Category category;
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z]+-\\d{3}$");

    public MenuItem(String id, String name, double cost, Category category) throws InvalidIdentifierException {
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new InvalidIdentifierException("Data Error: ID '" + id + "' does not match pattern");
        }
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public Category getCategory() { return category; }
}

class Order {
    private String customerId;
    private List<MenuItem> items;

    public Order(String customerId) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }
    public String getCustomerId() { return customerId; }
    public void addItem(MenuItem item) { this.items.add(item); }
    public List<MenuItem> getItems() { return items; }
    public double getRawTotal() {
        return items.stream().mapToDouble(MenuItem::getCost).sum();
    }
}

class DiscountEngine {
    public static double calculateFinalBill(Order order) {
        long foodCount = order.getItems().stream().filter(i -> i.getCategory() == Category.FOOD).count();
        long drinkCount = order.getItems().stream().filter(i -> i.getCategory() == Category.BEVERAGE).count();
        double rawTotal = order.getRawTotal();
        return (drinkCount >= 1 && foodCount >= 2) ? rawTotal * 0.80 : rawTotal;
    }
}