package org.example.services;

import org.example.models.Order;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentOrderProcessor {
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    private final Semaphore inventorySemaphore = new Semaphore(3);
    private final ConcurrentHashMap<Integer, ReentrantLock> productLocks = new ConcurrentHashMap<>();
    private final OrderService orderService = new OrderService();

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void runSimulation() {
        System.out.println("Starting Concurrent Order Processing Simulation...");
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 50; i++) {
                Order order = new Order();
                order.setOrderId(i);
                order.setProductId(1); 
                order.setQuantity(1);

                try {
                    orderQueue.put(order);
                    System.out.println("Producer generated Order ID: " + order.getOrderId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        producer.start();
        for (int i = 0; i < 50; i++) {
            executor.submit(new ConsumerTask());
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
            System.out.println("Simulation Completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class ConsumerTask implements Runnable {
        @Override
        public void run() {
            try {
                Order order = orderQueue.take();
                inventorySemaphore.acquire();
                try {
                    ReentrantLock lock = productLocks.computeIfAbsent(order.getProductId(), k -> new ReentrantLock());
                    lock.lock();
                    try {
                        System.out.println("Worker " + Thread.currentThread().getName() + " processing Order ID: " + order.getOrderId());
                        boolean success = orderService.processOrder(order.getProductId(), order.getQuantity());
                        if (success) {
                            System.out.println("Order ID: " + order.getOrderId() + " -> PROCESSED SUCCESSFULLY.");
                        } else {
                            System.out.println("Order ID: " + order.getOrderId() + " -> FAILED (Insufficient stock).");
                        }
                    } finally {
                        lock.unlock();
                    }
                } finally {
                    inventorySemaphore.release(); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
