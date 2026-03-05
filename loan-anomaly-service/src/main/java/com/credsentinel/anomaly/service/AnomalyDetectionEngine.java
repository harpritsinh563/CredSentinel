package com.credsentinel.anomaly.service;

import com.loan.anomaly.grpc.LoanAnomalyRequest;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnomalyDetectionEngine {

    // Simple in-memory storage for MVP velocity checks, later on replace it with redis
    private final ConcurrentHashMap<String, UserActivity> userActivityMap = new ConcurrentHashMap<>();

    public boolean isSuspicious(LoanAnomalyRequest request) {
        String userId = request.getUserId();
        Instant now = Instant.now();

        // --- Rule 1: Velocity Check (Rapid Fire Requests) ---
        // If the same user IDs makes 3+ requests in a very short window (e.g. 10 mins)
        UserActivity activity = userActivityMap.computeIfAbsent(userId, k -> new UserActivity(now));

        if (now.isBefore(activity.lastRequestTime.plusSeconds(600))) {
            int count = activity.requestCount.incrementAndGet();
            if (count > 3) {
                log.warn("Anomaly Detected: Velocity check failed for user {}", userId);
                return true;
            }
        } else {
            // Reset window if it's been more than 10 minutes
            activity.requestCount.set(1);
            activity.lastRequestTime = now;
        }

        // --- Rule 2: Impossible Journey / Geographic Risk ---
        // Example: If certain countries are flagged as high-risk for the current campaign
        if ("HIGH_RISK_ZONE".equalsIgnoreCase(request.getCountry())) {
            log.warn("Anomaly Detected: High-risk geographic origin for loan {}", request.getLoanId());
            return true;
        }

        // --- Rule 3: Round Number / Bot Pattern ---
        // Human behavior usually results in specific amounts.
        // Repeated "perfect" large round numbers or extremely specific amounts can be patterns.
        if (request.getLoanAmount() > 50000 && request.getLoanAmount() % 10000 == 0) {
            // This is a weak signal, usually combined with others, but good for an MVP "pattern"
            log.info("Flagging round number high-value request for manual review");
        }

        // --- Rule 4: Data Consistency (Synthetic Identity) ---
        // A user with 0 previous loans but a "perfect" 850 credit score is statistically rare
        // and often indicates a synthetic identity.
        if (request.getPreviousLoansCount() == 0 && request.getCreditScore() >= 800) {
            log.warn("Anomaly Detected: Potential synthetic identity for user {}", userId);
            return true;
        }

        return false;
    }

    private static class UserActivity {
        Instant lastRequestTime;
        AtomicInteger requestCount;

        UserActivity(Instant time) {
            this.lastRequestTime = time;
            this.requestCount = new AtomicInteger(1);
        }
    }
}