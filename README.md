# Concurrent Database-Driven Inventory Management System

## Project Overview
This project is a robust, concurrent inventory management system developed in Java. It connects to a MySQL database to manage products, process inventory operations (Stock In/Out), and handle customer orders safely in a multi-threaded environment. 

The system leverages the Java Collections Framework, the Stream API for advanced reporting, and concurrency controls (`Semaphores`, `ReentrantLocks`, and `ConcurrentHashMap`) to ensure data consistency and prevent race conditions (like overselling) when multiple workers access the inventory simultaneously.

## Features
- **Product Management (CRUD):** Add, update, delete, and search products easily.
- **Inventory Operations:** Safely Stock In and Stock Out products with automated action logging.
- **Concurrent Order Processing (Module 6):** Simulates 50 simultaneous customer orders using an `ExecutorService` (Thread Pool). It utilizes a Producer-Consumer pattern with a `LinkedBlockingQueue` and `Semaphore` to guarantee thread-safe inventory updates.
- **Advanced Reporting (Streams):** Uses Java Streams to generate reports for the top 5 most expensive products, low stock alerts, category groupings, and total inventory value.
- **Robust Data Validation:** A custom `InvalidDataException` ensures no negative quantities or prices can be entered into the system. The console interactively reprompts the user until valid data is supplied.
- **Auto-Cleanup:** The application automatically checks and corrects corrupted negative database values upon startup.
- **Interactive Console UI:** A complete, menu-driven command-line interface to test all modules seamlessly.

## Sample Usage

To run the application, ensure your MySQL database is running, configure your credentials in `DBConnection.java`, and execute the `Main.java` class.

### The Main Menu
When you start the application, the system performs a health check and presents the main menu:
```text
Initializing system... Checking for corrupted data...
[System Initialization] Fixed 2 products with negative inventory.

---------- INVENTORY MANAGEMENT SYSTEM ----------
1. Product Management
2. Inventory Operations
3. Process Single Order
4. Reports & Stream Operations
5. Run Concurrent Order Simulation (Module 6)
0. Exit
Select an option: 
```

### Validating Inputs
The system catches invalid inputs instantly:
```text
Select an option: 2

--- Inventory Operations ---
1. Stock In
2. Stock Out
Select an option: 2
Product ID: 5
Quantity: -10
Error: Stock Out quantity must be strictly greater than zero. Please try again.
Quantity: 10
Stock OUT successful!
```

### Running the Multithreaded Simulation
Test the core concurrency challenge by selecting Option 5:
```text
Select an option: 5

--- Running Multithreaded Module 6 Simulation ---
[Thread-2] Processing order for Product ID: 4, Qty: 2
[Thread-5] Processing order for Product ID: 4, Qty: 5
...
[Thread-2] Order SUCCESS!
[Thread-5] Order FAILED! Not enough stock.
```
