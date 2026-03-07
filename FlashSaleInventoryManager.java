import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlashSaleInventoryManager {

    // productId -> stock count
    private ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>();

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    // Add product with stock
    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock > 0) {
            stock.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    // View waiting list
    public Queue<Integer> getWaitingList(String productId) {
        return waitingList.get(productId);
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // simulate stock exhaustion
        for (int i = 0; i < 98; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}