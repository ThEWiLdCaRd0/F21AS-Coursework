import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Secondary class: No 'public' keyword
enum Category {
    FOOD, BEVERAGE, OTHER
}

// Secondary class: No 'public' keyword
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
        double total = 0;
        for (MenuItem item : items) {
            total += item.getCost();
        }
        return total;
    }
}

// Main class: MUST be public
public class MenuItem {
    private String id;
    private String name;
    private double cost;
    private Category category;

    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z]+-\\d{3}$");

    public MenuItem(String id, String name, double cost, Category category) throws InvalidIdentifierException {
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new InvalidIdentifierException("Data Error: ID does not match pattern");
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