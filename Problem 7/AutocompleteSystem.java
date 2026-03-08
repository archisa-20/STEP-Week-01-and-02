import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequencyMap = new HashMap<>();
    private static final int TOP_K = 10;

    // Insert query into Trie
    public void addQuery(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, frequencyMap.get(query));
        }

        node.isEnd = true;
    }

    // Search prefix suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(node.queries.entrySet());

        List<String> results = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < TOP_K) {
            Map.Entry<String, Integer> entry = pq.poll();
            results.add(entry.getKey() + " (" + entry.getValue() + " searches)");
            count++;
        }

        return results;
    }

    // Update frequency when query searched again
    public void updateFrequency(String query) {
        addQuery(query);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        System.out.println("Suggestions for 'jav':");
        List<String> suggestions = system.search("jav");

        for (String s : suggestions) {
            System.out.println(s);
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter updating frequency:");
        suggestions = system.search("jav");

        for (String s : suggestions) {
            System.out.println(s);
        }
    }
}