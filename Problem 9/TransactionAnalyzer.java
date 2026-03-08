import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time; // timestamp in milliseconds

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }

    public String toString() {
        return "id:" + id + " amount:" + amount;
    }
}

public class TransactionAnalyzer {

    // Classic Two-Sum
    public static List<List<Transaction>> findTwoSum(List<Transaction> transactions, int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum with time window (1 hour)
    public static List<List<Transaction>> findTwoSumTimeWindow(List<Transaction> transactions, int target, long windowMillis) {

        List<List<Transaction>> result = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {

            Transaction t1 = transactions.get(i);

            for (int j = i + 1; j < transactions.size(); j++) {

                Transaction t2 = transactions.get(j);

                if (Math.abs(t1.time - t2.time) <= windowMillis &&
                        t1.amount + t2.amount == target) {

                    result.add(Arrays.asList(t1, t2));
                }
            }
        }

        return result;
    }

    // Duplicate detection
    public static Map<String, List<Transaction>> detectDuplicates(List<Transaction> transactions) {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        Map<String, List<Transaction>> duplicates = new HashMap<>();

        for (String key : map.keySet()) {
            if (map.get(key).size() > 1) {
                duplicates.put(key, map.get(key));
            }
        }

        return duplicates;
    }

    // K-Sum using recursion
    public static List<List<Transaction>> findKSum(List<Transaction> transactions, int k, int target) {

        List<List<Transaction>> result = new ArrayList<>();
        kSumHelper(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void kSumHelper(List<Transaction> transactions, int k, int target,
                                  int start, List<Transaction> current,
                                  List<List<Transaction>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || start >= transactions.size()) {
            return;
        }

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t);

            kSumHelper(transactions, k - 1, target - t.amount, i + 1, current, result);

            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        long now = System.currentTimeMillis();

        transactions.add(new Transaction(1, 500, "Store A", "acc1", now));
        transactions.add(new Transaction(2, 300, "Store B", "acc2", now + 1000));
        transactions.add(new Transaction(3, 200, "Store C", "acc3", now + 2000));
        transactions.add(new Transaction(4, 500, "Store A", "acc4", now + 3000));

        System.out.println("Two Sum:");
        System.out.println(findTwoSum(transactions, 500));

        System.out.println("\nTwo Sum (1 hour window):");
        System.out.println(findTwoSumTimeWindow(transactions, 500, 3600000));

        System.out.println("\nDuplicate Detection:");
        System.out.println(detectDuplicates(transactions));

        System.out.println("\nK Sum (k=3, target=1000):");
        System.out.println(findKSum(transactions, 3, 1000));
    }
}