import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class SimulationGUI extends JFrame implements ObserverInterfaces {
    
    private OrderQueue orderQueue;
    private List<Server> servers;
    
    private DefaultListModel<String> queueListModel;
    private JPanel serversPanel;
    private JTextArea[] serverDisplays;

    public SimulationGUI(OrderQueue orderQueue, List<Server> servers) {
        this.orderQueue = orderQueue;
        this.servers = servers;
        
        // Register this GUI as an observer
        this.orderQueue.registerObserver(this);
        for (Server s : servers) {
            s.registerObserver(this);
        }

        setTitle("Coffee Shop Live Simulation (Stage 2)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(40, 44, 52));

        // 1. LEFT PANEL: Customer Queue
        queueListModel = new DefaultListModel<>();
        JList<String> queueList = new JList<>(queueListModel);
        queueList.setFont(new Font("SansSerif", Font.BOLD, 14));
        queueList.setBackground(new Color(30, 33, 39));
        queueList.setForeground(Color.WHITE);
        
        JScrollPane queueScroll = new JScrollPane(queueList);
        queueScroll.setPreferredSize(new Dimension(300, 0));
        queueScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Customers Waiting", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 16), Color.WHITE));
        add(queueScroll, BorderLayout.WEST);

        // 2. CENTER PANEL: Servers
        serversPanel = new JPanel(new GridLayout(1, servers.size(), 10, 0));
        serversPanel.setOpaque(false);
        serverDisplays = new JTextArea[servers.size()];

        for (int i = 0; i < servers.size(); i++) {
            serverDisplays[i] = new JTextArea();
            serverDisplays[i].setEditable(false);
            serverDisplays[i].setFont(new Font("Monospaced", Font.PLAIN, 14));
            serverDisplays[i].setBackground(new Color(255, 253, 231));
            serverDisplays[i].setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), servers.get(i).getServerName(), 
                TitledBorder.CENTER, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 16), Color.BLACK));
            serversPanel.add(new JScrollPane(serverDisplays[i]));
        }
        add(serversPanel, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Extended Requirement (Simulation Speed)
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        JLabel speedLabel = new JLabel("Simulation Speed: ");
        speedLabel.setForeground(Color.WHITE);
        JSlider speedSlider = new JSlider(1, 10, 1); // 1x to 10x speed
        speedSlider.setOpaque(false);
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> SimulationController.setSpeedMultiplier(speedSlider.getValue()));
        
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        add(controlPanel, BorderLayout.SOUTH);
    }

    // OBSERVER PATTERN: This triggers automatically when Models change
    @Override
    public void update() {
        // MUST use invokeLater for Swing thread safety!
        SwingUtilities.invokeLater(() -> {
            // Update Queue UI
            queueListModel.clear();
            for (Order o : orderQueue.getQueueSnapshot()) {
                queueListModel.addElement(o.getCustomerId() + " (" + o.getItems().size() + " items)");
            }
            if (queueListModel.isEmpty()) {
                queueListModel.addElement("Queue is empty.");
            }

            // Update Server UIs
            for (int i = 0; i < servers.size(); i++) {
                serverDisplays[i].setText(servers.get(i).getCurrentStatus());
            }
        });
    }
}