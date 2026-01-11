package pnvteck.all_in_one.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pnvteck.all_in_one.common.config.RateLimitProperties;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    private final RateLimitProperties properties;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final AtomicLong lastCleanup = new AtomicLong(0);

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        long windowMs = properties.getWindowSeconds() * 1000L;
        int limit = properties.getRequests();

        String key = resolveClientKey(request);
        Counter counter = counters.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStartMs >= windowMs) {
                return new Counter(now);
            }
            return existing;
        });

        int current = counter.count.incrementAndGet();
        int remaining = Math.max(0, limit - current);
        response.setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));

        if (current > limit) {
            long retryAfterSeconds = Math.max(
                    1,
                    (windowMs - (now - counter.windowStartMs) + 999) / 1000
            );
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write("Too many requests");
            return;
        }

        cleanupOldCounters(now, windowMs);
        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isBlank()) {
            int commaIndex = forwarded.indexOf(',');
            return commaIndex > 0 ? forwarded.substring(0, commaIndex).trim() : forwarded.trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupOldCounters(long now, long windowMs) {
        long last = lastCleanup.get();
        if (now - last < windowMs) {
            return;
        }
        if (!lastCleanup.compareAndSet(last, now)) {
            return;
        }
        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStartMs >= windowMs);
    }

    private static final class Counter {
        private final long windowStartMs;
        private final AtomicInteger count = new AtomicInteger(0);

        private Counter(long windowStartMs) {
            this.windowStartMs = windowStartMs;
        }
    }
}
