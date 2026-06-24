package org.example;

import org.example.collections.ProductRepository;
import org.example.exceptions.InvalidDataException;
import org.example.models.Product;
import org.example.services.ConcurrentOrderProcessor;
import org.example.services.InventoryService;
import org.example.services.OrderService;
import org.example.services.ProductService;
import org.example.services.ReportService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductService productService = new ProductService();
    private static final InventoryService inventoryService = new InventoryService();
    private static final OrderService orderService = new OrderService();
    private static final ReportService reportService = new ReportService();
    private static final ProductRepository productRepository = new ProductRepository();

    public static void main(String[] args) {
        System.out.println("Initializing system... Checking for corrupted data...");
        inventoryService.fixNegativeInventory();
        
        while (true) {
            System.out.println("\n---------- INVENTORY MANAGEMENT SYSTEM ----------");
            System.out.println("1. Product Management");
            System.out.println("2. Inventory Operations");
            System.out.println("3. Process Single Order");
            System.out.println("4. Reports & Stream Operations");
            System.out.println("5. Concurrent Order Simulation");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        productMenu();
                        break;
                    case 2:
                        inventoryMenu();
                        break;
                    case 3:
                        processOrder();
                        break;
                    case 4:
                        reportMenu();
                        break;
                    case 5:
                        runSimulation();
                        break;
                    case 0:
                        System.out.println("Exiting System...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void productMenu() {
        System.out.println("\n--- Product Management ---");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. Search Product by ID");
        System.out.println("5. Search Product by Name");
        System.out.println("6. Search Product by Category");
        System.out.print("Select an option: ");

        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1:
                System.out.print("Name: "); String name = scanner.nextLine();
                System.out.print("Category: "); String category = scanner.nextLine();
                int qty = readValidQuantity();
                double price = readValidPrice();
                productService.addProduct(new Product(0, name, category, qty, price));
                break;
            case 2:
                int id = readValidProductId("Product ID to Update: ");
                Product existing = productService.searchById(id);
                if (existing == null) {
                    System.out.println("Product not found!");
                    break;
                }
                System.out.print("New Name [" + existing.getName() + "]: "); 
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) newName = existing.getName();

                System.out.print("New Category [" + existing.getCategory() + "]: "); 
                String newCategory = scanner.nextLine().trim();
                if (newCategory.isEmpty()) newCategory = existing.getCategory();

                int newQty = readValidQuantity(existing.getQuantity());
                double newPrice = readValidPrice(existing.getPrice());
                productService.updateProduct(new Product(id, newName, newCategory, newQty, newPrice));
                break;
            case 3:
                int delId = readValidProductId("Product ID to Delete: ");
                productService.deleteProduct(delId);
                break;
            case 4:
                int searchId = readValidProductId("Enter Product ID: ");
                System.out.println(productService.searchById(searchId));
                break;
            case 5:
                System.out.print("Enter Name: "); String searchName = scanner.nextLine();
                productService.searchByName(searchName).forEach(System.out::println);
                break;
            case 6:
                System.out.print("Enter Category: "); String searchCat = scanner.nextLine();
                productService.searchByCategory(searchCat).forEach(System.out::println);
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void inventoryMenu() {
        System.out.println("\n--- Inventory Operations ---");
        System.out.println("1. Stock In");
        System.out.println("2. Stock Out");
        System.out.print("Select an option: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice != 1 && choice != 2) {
            System.out.println("Invalid choice.");
            return;
        }
        
        int id = readValidProductId("Product ID: ");
        int qty = readValidQuantity();

        if (choice == 1) 
            inventoryService.stockIn(id, qty);
        else if (choice == 2) 
            inventoryService.stockOut(id, qty);
    }

    private static void processOrder() {
        System.out.println("\n--- Process Single Order ---");
        int id = readValidProductId("Product ID: ");
        int qty = readValidQuantity();
        
        boolean success = orderService.processOrder(id, qty);
        if (success) System.out.println("Order processed successfully!");
        else System.out.println("Order failed! Insufficient stock or invalid product.");
    }

    private static void reportMenu() {
        System.out.println("\n--- Reports & Stream Operations ---");
        List<Product> products = productRepository.getAllProducts();
        if (products == null || products.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }

        System.out.println("1. Top 5 Expensive Products");
        System.out.println("2. Products by Category");
        System.out.println("3. Low Stock Products");
        System.out.println("4. Total Inventory Value");
        System.out.print("Select an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1: reportService.top5Expensive(products); break;
            case 2: reportService.groupByCategory(products); break;
            case 3: reportService.lowStock(products); break;
            case 4: reportService.inventoryValue(products); break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void runSimulation() {
        System.out.println("\n--- Running Multithreaded Module 6 Simulation ---");
        ConcurrentOrderProcessor processor = new ConcurrentOrderProcessor();
        processor.runSimulation();
    }

    private static int readValidProductId(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int id = Integer.parseInt(scanner.nextLine());
                if (id <= 0) {
                    throw new InvalidDataException("Product ID must be greater than zero.");
                }
                if (productService.searchById(id) == null) {
                    System.out.println("Error: Product ID " + id + " does not exist. Please try again.");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for Product ID.");
            } catch (InvalidDataException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static int readValidQuantity() {
        return readValidQuantity(null);
    }

    private static int readValidQuantity(Integer defaultValue) {
        while (true) {
            try {
                if (defaultValue == null) {
                    System.out.print("Quantity: ");
                } else {
                    System.out.print("New Quantity [" + defaultValue + "]: ");
                }
                String input = scanner.nextLine().trim();
                if (input.isEmpty() && defaultValue != null) {
                    return defaultValue;
                }
                int qty = Integer.parseInt(input);
                if (qty < 0) {
                    throw new InvalidDataException("Product quantity cannot be negative.");
                }
                return qty;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for quantity.");
            } catch (InvalidDataException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static double readValidPrice() {
        return readValidPrice(null);
    }

    private static double readValidPrice(Double defaultValue) {
        while (true) {
            try {
                if (defaultValue == null) {
                    System.out.print("Price: ");
                } else {
                    System.out.print("New Price [" + defaultValue + "]: ");
                }
                String input = scanner.nextLine().trim();
                if (input.isEmpty() && defaultValue != null) {
                    return defaultValue;
                }
                double price = Double.parseDouble(input);
                if (price < 0) {
                    throw new InvalidDataException("Product price cannot be negative.");
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for price.");
            } catch (InvalidDataException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }
    }
}