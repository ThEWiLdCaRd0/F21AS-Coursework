import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// SINGLETON PATTERN: Ensures only one log exists and is thread-safe
public class Logger {
    private static Logger instance;
    private List<String> logEntries;

    private Logger() {
        logEntries = new ArrayList<>();
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public synchronized void logEvent(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = "[" + timestamp + "] " + event;
        logEntries.add(entry);
        System.out.println(entry); // Print to console for debugging
    }

    public synchronized void writeLogToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter("simulation_log.txt"))) {
            for (String entry : logEntries) {
                out.println(entry);
            }
            System.out.println("Log successfully saved to simulation_log.txt");
        } catch (IOException e) {
            System.err.println("Failed to write log file.");
        }
    }
}