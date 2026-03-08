import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int tokens;
    final int maxTokens;
    final double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRatePerHour) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = refillRatePerHour / 3600.0; // convert to per second
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    private void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    public int getRemainingTokens() {
        refill();
        return tokens;
    }
}

public class RateLimiter {

    private static final int LIMIT_PER_HOUR = 1000;

    private ConcurrentHashMap<String, TokenBucket> clientBuckets =
            new ConcurrentHashMap<>();

    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(LIMIT_PER_HOUR, LIMIT_PER_HOUR));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            return "No usage yet";
        }

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT_PER_HOUR - remaining;

        return "{used: " + used +
                ", limit: " + LIMIT_PER_HOUR +
                ", remaining: " + remaining + "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        System.out.println(limiter.getRateLimitStatus(client));
    }
}