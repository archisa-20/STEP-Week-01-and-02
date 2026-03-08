import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCache {

    // L1 Cache (10,000 videos) – in memory
    private LinkedHashMap<String, VideoData> L1;

    // L2 Cache (100,000 videos) – simulated SSD
    private LinkedHashMap<String, VideoData> L2;

    // L3 Database (all videos)
    private Map<String, VideoData> database = new HashMap<>();

    private int L1_CAPACITY = 10000;
    private int L2_CAPACITY = 100000;

    private int L1Hits = 0, L2Hits = 0, L3Hits = 0;
    private int totalRequests = 0;

    public MultiLevelCache() {

        L1 = new LinkedHashMap<String, VideoData>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        L2 = new LinkedHashMap<String, VideoData>(L2_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L2_CAPACITY;
            }
        };
    }

    // Simulated database insert
    public void addVideoToDB(String id, String content) {
        database.put(id, new VideoData(id, content));
    }

    // Get video from cache hierarchy
    public VideoData getVideo(String videoId) {

        totalRequests++;

        // L1 Check
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        // L2 Check
        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");

            VideoData data = L2.get(videoId);
            L1.put(videoId, data);
            return data;
        }

        // L3 Database
        if (database.containsKey(videoId)) {
            L3Hits++;
            System.out.println("L3 Database HIT → Added to L2");

            VideoData data = database.get(videoId);
            L2.put(videoId, data);
            return data;
        }

        System.out.println("Video not found");
        return null;
    }

    // Cache invalidation
    public void invalidate(String videoId) {
        L1.remove(videoId);
        L2.remove(videoId);
        database.remove(videoId);
    }

    // Statistics
    public void getStatistics() {

        double L1Rate = (L1Hits * 100.0) / totalRequests;
        double L2Rate = (L2Hits * 100.0) / totalRequests;
        double L3Rate = (L3Hits * 100.0) / totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3Rate) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideoToDB("video_123", "Movie A");
        cache.addVideoToDB("video_999", "Movie B");

        cache.getVideo("video_123"); // DB → L2
        cache.getVideo("video_123"); // L2 → L1
        cache.getVideo("video_123"); // L1

        cache.getVideo("video_999"); // DB → L2

        cache.getStatistics();
    }
}