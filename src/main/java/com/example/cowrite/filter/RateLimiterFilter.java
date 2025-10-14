package com.example.cowrite.filter;

import com.example.cowrite.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    @Value("${rate.limit.requests:30}")
    private int MAX_REQUESTS;
    @Value("${rate.limit.duration.seconds:30}")
    private int DURATION_SECONDS;
    @Value("${rate.limit.refill:10}")
    private int REFILL;

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(MAX_REQUESTS).refillGreedy(REFILL, Duration.ofSeconds(DURATION_SECONDS)))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();

        Bucket bucket = ipBuckets.computeIfAbsent(ip, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            ErrorDTO errorDTO = new ErrorDTO(
                    LocalDateTime.now(ZoneOffset.UTC),
                    429,
                    "Too many requests"
            );

            String json = objectMapper.writeValueAsString(errorDTO);
            response.getWriter().write(json);
            response.getWriter().flush();
        }
    }
}
