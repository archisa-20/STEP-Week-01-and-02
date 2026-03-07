import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {

    // username -> userId
    private ConcurrentHashMap<String, Integer> users = new ConcurrentHashMap<>();

    // username -> attempt frequency
    private ConcurrentHashMap<String, Integer> attemptFrequency = new ConcurrentHashMap<>();

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Update attempt count
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // Check existence
        return !users.containsKey(username);
    }

    // Register user
    public void registerUser(String username, int userId) {
        users.put(username, userId);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String newName = username + i;

            if (!users.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        // Replace underscore with dot
        String modified = username.replace("_", ".");
        if (!users.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result + " (" + max + " attempts)";
    }

    // Test program
    public static void main(String[] args) {

        UsernameChecker system = new UsernameChecker();

        system.registerUser("john_doe", 101);
        system.registerUser("alice99", 102);

        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        System.out.println(system.suggestAlternatives("john_doe"));

        System.out.println(system.getMostAttempted());
    }
}