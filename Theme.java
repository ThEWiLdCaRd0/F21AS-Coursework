import java.awt.Color;
import java.awt.Font;

public class Theme {
    public static final Color BG_DARK = new Color(30, 30, 30);         // Sleek Dark Grey Background
    public static final Color TEXT_LIGHT = new Color(240, 240, 240);   // Off-white text for dark backgrounds
    public static final Color BORDER_COLOR = new Color(100, 100, 100); // Subtle grey for panel borders
    
    // Item Category Colors (Pastels that pop on dark backgrounds)
    public static final Color BTN_BEVERAGE = new Color(100, 180, 255); // Vibrant Light Blue
    public static final Color BTN_FOOD = new Color(144, 238, 144);     // Vibrant Light Green
    public static final Color BTN_OTHER = new Color(255, 200, 100);    // Vibrant Light Orange
    public static final String BTN_TEXT_HEX = "#141414";               // Dark text for inside the buttons
    
    // Cart & Receipt Colors
    public static final Color RECEIPT_BG = new Color(255, 253, 231);   // Pale yellow
    public static final Color RECEIPT_TEXT = new Color(0, 0, 0);
    
    // Action Buttons & Highlights
    public static final Color CHECKOUT_BTN = new Color(39, 174, 96);   // Modern Green
    public static final Color CLOSE_BTN = new Color(70, 70, 70);       // Darker grey for close button
    public static final Color TOTAL_RED = new Color(255, 100, 100);    // Lighter red to contrast dark BG
    public static final Color TOTAL_GREEN = new Color(80, 220, 120);   // Lighter green for discount
    
    // Global Fonts
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font RECEIPT_FONT = new Font("Monospaced", Font.BOLD, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 18);
}