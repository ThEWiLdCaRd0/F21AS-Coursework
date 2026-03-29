import java.awt.Color;
import java.awt.Font;

// STAGE 2 THEME: Centralized UI constants for easy manageability
public class Theme {
    
    // --- Global Backgrounds & Text ---
    public static final Color BG_DARK = new Color(40, 44, 52);
    public static final Color BG_PANEL = new Color(30, 33, 39);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color BORDER_COLOR = Color.GRAY;
    
    // --- Category Button Colors ---
    public static final Color BTN_BEVERAGE = new Color(70, 150, 220); // Blue
    public static final Color BTN_FOOD = new Color(100, 200, 100);    // Green
    public static final Color BTN_OTHER = new Color(220, 170, 70);    // Orange
    public static final String BTN_TEXT_HEX = "#FFFFFF";              // Hex required for HTML rendering
    
    // --- Cart & Receipt Elements ---
    public static final Color RECEIPT_BG = new Color(30, 33, 39);
    public static final Color RECEIPT_TEXT = Color.WHITE;
    public static final Color CHECKOUT_BTN = new Color(39, 174, 96);  // Bright Green
    public static final Color CLOSE_BTN = new Color(192, 57, 43);     // Red
    public static final Color TOTAL_GREEN = new Color(80, 220, 120);  // Pastel Green for Discounts
    
    // --- Global Fonts ---
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font RECEIPT_FONT = new Font("Monospaced", Font.PLAIN, 14);
}