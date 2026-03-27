COFFEE SHOP SIMULATION (MULTI-THREADED POS)


Overview
This application is a multi-threaded Java Swing simulation of a live coffee shop environment. It demonstrates advanced Object-Oriented Programming (OOP) concepts, including concurrency (Producer-Consumer model), custom exception handling, File I/O, and strict adherence to architectural design patterns (MVC, Observer, and Singleton).


Features
Live Simulation (Multi-threading): Automated customers trickle into a synchronized queue while Barista threads (Consumers) process them in real-time.

Interactive POS (ManualOrderWindow): A live secondary GUI allows human users to construct orders, apply automated discounts, and inject them safely into the live thread queue.


Design Patterns Used:
MVC: Separates the data models, Swing views, and the SimulationController.
Observer: The GUI automatically updates whenever the OrderQueue or Barista threads change state.
Singleton: A globally accessible, thread-safe Logger records all events.
File Persistence: Reads starting data from menu.csv and orders.csv. Live manual orders are appended directly to orders.csv, and an end-of-day report is written to simulation_log.txt.
Dynamic Discounts ("The Meal Deal"): Automatically applies a 20% discount if a cart contains at least 1 Beverage and 2 Food items.


Prerequisites
Java Development Kit (JDK) 8 or higher installed on your machine.
All .java and .csv files must be located in the exact same root directory.


Required Files
Make sure the following files are present in your project folder before running:
SimulationController.java (Main Entry Point)
SimulationGUI.java
ManualOrderWindow.java
SimulationThreads.java
OrderQueue.java
Models.java
MenuLoader.java
Logger.java
ObserverInterfaces.java
menu.csv (Data file)
orders.csv (Data file)


How to Compile and Run (Command Line / Terminal)
Open your terminal or command prompt.
Navigate to the directory containing all the project files:
cd path/to/your/project/folder

Compile all the Java files:
javac *.java

Run the main Simulation Controller:
java SimulationController

How to Compile and Run (IDE - VS Code, Eclipse, IntelliJ)
Open the project folder in your IDE.
Ensure your IDE is set to use the current folder as the working directory (so it can find the .csv files).
Locate SimulationController.java in your file explorer.
Click "Run" or "Run Java" on the SimulationController class.


Usage Instructions
Simulation Dashboard: Watch the automated customers join the queue on the left, while Barista 1 and Barista 2 process them on the right.
Speed Control: Use the slider at the bottom of the main dashboard to speed up or slow down the Baristas' processing times.
Live Point of Sale: Use the "Live Point of Sale" window to manually add items to a cart. Click "Send Order to Baristas" to inject it into the live queue.
Closing the Shop: Click "Close Shop (Stop Simulation)" in the POS window to lock the doors. The Baristas will finish the remaining queue, save the simulation_log.txt file, and gracefully shut down the program.