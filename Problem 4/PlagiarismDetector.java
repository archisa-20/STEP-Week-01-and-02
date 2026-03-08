import java.util.*;

public class PlagiarismDetector {

    // n-gram -> set of document IDs
    private Map<String, Set<String>> ngramIndex = new HashMap<>();

    // document -> its n-grams
    private Map<String, List<String>> documentNgrams = new HashMap<>();

    private int N = 5; // 5-gram

    // Extract n-grams from a document
    private List<String> generateNgrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze new document for plagiarism
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String existingDoc : ngramIndex.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            int total = documentNgrams.get(doc).size();

            double similarity = (matches * 100.0) / total;

            System.out.println("Found " + matches + " matching n-grams with \"" + doc + "\"");
            System.out.println("Similarity: " + String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument("essay_089.txt",
                "Artificial intelligence is transforming the world with advanced machine learning systems");

        detector.addDocument("essay_092.txt",
                "Artificial intelligence is transforming the world with advanced machine learning systems and automation");

        detector.analyzeDocument("essay_123.txt",
                "Artificial intelligence is transforming the world with advanced machine learning systems");
    }
}