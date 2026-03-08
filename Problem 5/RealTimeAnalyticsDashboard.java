import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    // pageUrl -> visit count
    private Map<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique visitors
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();

    // source -> visit count
    private Map<String, Integer> trafficSources = new HashMap<>();

    // Process incoming event
    public void processEvent(PageViewEvent event) {

        // Update page view count
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 pages
    public List<String> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<String> result = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {

            Map.Entry<String, Integer> entry = pq.poll();

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            result.add(url + " - " + views + " views (" + unique + " unique)");

            count++;
        }

        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<String> topPages = getTopPages();

        int rank = 1;
        for (String page : topPages) {
            System.out.println(rank + ". " + page);
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (String source : trafficSources.keySet()) {
            System.out.println(source + " - " + trafficSources.get(source));
        }
    }

    public static void main(String[] args) {

        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();

        dashboard.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new PageViewEvent("/sports/championship", "user_123", "google"));
        dashboard.processEvent(new PageViewEvent("/sports/championship", "user_789", "direct"));
        dashboard.processEvent(new PageViewEvent("/article/breaking-news", "user_999", "google"));

        dashboard.getDashboard();
    }
}