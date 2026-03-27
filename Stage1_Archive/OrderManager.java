package Stage1_Archive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Secondary class: No 'public' keyword
class DiscountEngine {
public static double calculateFinalBill(Order order) {
int foodCount = 0;
int drinkCount = 0;
double rawTotal = order.getRawTotal();

for (MenuItem item : order.getItems()) {
if (item.getCategory() == Category.FOOD) foodCount++;
if (item.getCategory() == Category.BEVERAGE) drinkCount++;
}

if (drinkCount >= 1 && foodCount >= 2) {
return rawTotal * 0.80;
}
return rawTotal;
}
}

// Main class: MUST be public
public class OrderManager {
private List<Order> orderHistory = new ArrayList<>();

public void addOrder(Order order) {
orderHistory.add(order);
}

public String generateSummaryReport() {
double totalRevenue = 0;
Map<String, Integer> itemCounts = new HashMap<>();

for (Order order : orderHistory) {
totalRevenue += DiscountEngine.calculateFinalBill(order);
for (MenuItem item : order.getItems()) {
itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
}
}

StringBuilder report = new StringBuilder("=== END OF DAY SUMMARY ===\n");
report.append(String.format("Total Revenue: £%.2f\n", totalRevenue));
report.append("Total Orders: ").append(orderHistory.size()).append("\n\n");
for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
}
return report.toString();
}
}