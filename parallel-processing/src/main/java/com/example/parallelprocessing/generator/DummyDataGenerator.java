package com.example.parallelprocessing.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DummyDataGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String[] DEPARTMENTS = {"Engineering", "Marketing", "Sales", "HR", "Finance", "Operations"};
    private static final String[] POSITIONS = {"Junior Developer", "Senior Developer", "Team Lead", "Manager", "Director"};
    private static final String[] CITIES = {"Seoul", "Busan", "Incheon", "Daegu", "Daejeon", "Gwangju"};
    private static final String[] CATEGORIES = {"ELECTRONICS", "BOOKS", "CLOTHING", "FOOD", "SPORTS", "BEAUTY"};
    private static final String[] TX_TYPES = {"PURCHASE", "REFUND", "EXCHANGE", "SUBSCRIPTION"};
    private static final String[] TX_STATUSES = {"COMPLETED", "PENDING", "FAILED", "CANCELLED"};
    private static final String[] LOG_LEVELS = {"INFO", "WARN", "ERROR", "DEBUG"};
    private static final String[] LOG_MESSAGES = {
            "User logged in", "Viewed dashboard", "Started transaction",
            "Payment retry", "Transaction completed", "Session refreshed",
            "Profile updated", "Settings changed", "Notification sent",
            "Data exported", "Report generated", "Cache cleared",
            "API call made", "File uploaded", "Search performed",
            "Cart updated", "Checkout initiated", "Order confirmed"
    };

    public Map<String, Object> generate(int fileId) {
        Random random = new Random(fileId);
        String batchId = String.format("batch_%03d", (fileId - 1) / 100 + 1);
        LocalDateTime baseTime = LocalDateTime.of(2026, 2, 27, 8, 0, 0).plusMinutes(fileId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", fileId);
        data.put("uuid", UUID.nameUUIDFromBytes(("poc-" + fileId).getBytes()).toString());
        data.put("timestamp", baseTime.format(FMT));
        data.put("metadata", generateMetadata(fileId, batchId));
        data.put("user", generateUser(fileId, random));
        data.put("transactions", generateTransactions(fileId, random, baseTime));
        data.put("analytics", generateAnalytics(random));
        data.put("settings", generateSettings(random));
        data.put("logs", generateLogs(random, baseTime));
        data.put("systemInfo", generateSystemInfo(fileId, random));

        return data;
    }

    private Map<String, Object> generateMetadata(int fileId, String batchId) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("source", "poc-generator");
        metadata.put("version", "1.0");
        metadata.put("batchId", batchId);
        metadata.put("sequenceNumber", fileId);
        metadata.put("generatedAt", LocalDateTime.now().format(FMT));
        return metadata;
    }

    private Map<String, Object> generateUser(int fileId, Random random) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("userId", 10000 + fileId);
        user.put("username", "user_" + (10000 + fileId));
        user.put("email", "user_" + (10000 + fileId) + "@example.com");

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("firstName", "FirstName" + fileId);
        profile.put("lastName", "LastName" + fileId);
        profile.put("age", 22 + random.nextInt(40));
        profile.put("department", DEPARTMENTS[random.nextInt(DEPARTMENTS.length)]);
        profile.put("position", POSITIONS[random.nextInt(POSITIONS.length)]);
        profile.put("phoneNumber", String.format("010-%04d-%04d", random.nextInt(10000), random.nextInt(10000)));
        user.put("profile", profile);

        Map<String, Object> address = new LinkedHashMap<>();
        String city = CITIES[random.nextInt(CITIES.length)];
        address.put("street", (random.nextInt(500) + 1) + " Main St");
        address.put("city", city);
        address.put("district", "District-" + (random.nextInt(20) + 1));
        address.put("zipCode", String.format("%05d", 10000 + random.nextInt(90000)));
        address.put("country", "KR");
        user.put("address", address);

        return user;
    }

    private List<Map<String, Object>> generateTransactions(int fileId, Random random, LocalDateTime baseTime) {
        int txCount = 3 + random.nextInt(4); // 3~6 transactions
        List<Map<String, Object>> transactions = new ArrayList<>();

        for (int i = 0; i < txCount; i++) {
            Map<String, Object> tx = new LinkedHashMap<>();
            tx.put("transactionId", String.format("txn_%05d_%02d", fileId, i + 1));
            tx.put("type", TX_TYPES[random.nextInt(TX_TYPES.length)]);
            int amount = (random.nextInt(100) + 1) * 1000;
            tx.put("amount", amount);
            tx.put("currency", "KRW");
            tx.put("status", TX_STATUSES[random.nextInt(TX_STATUSES.length)]);
            tx.put("createdAt", baseTime.plusMinutes(i * 30L).format(FMT));

            int itemCount = 1 + random.nextInt(3); // 1~3 items
            List<Map<String, Object>> items = new ArrayList<>();
            for (int j = 0; j < itemCount; j++) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("itemId", String.format("item_%05d_%02d_%02d", fileId, i + 1, j + 1));
                item.put("name", "Product_" + (char) ('A' + random.nextInt(26)) + (j + 1));
                item.put("quantity", 1 + random.nextInt(5));
                item.put("unitPrice", (random.nextInt(50) + 1) * 1000);
                item.put("category", CATEGORIES[random.nextInt(CATEGORIES.length)]);
                items.add(item);
            }
            tx.put("items", items);
            transactions.add(tx);
        }
        return transactions;
    }

    private Map<String, Object> generateAnalytics(Random random) {
        Map<String, Object> analytics = new LinkedHashMap<>();
        int totalTx = 3 + random.nextInt(4);
        int totalAmount = (random.nextInt(500) + 50) * 1000;
        analytics.put("totalTransactions", totalTx);
        analytics.put("totalAmount", totalAmount);
        analytics.put("averageAmount", totalAmount / totalTx);

        List<String> categories = new ArrayList<>();
        int catCount = 2 + random.nextInt(4);
        for (int i = 0; i < catCount; i++) {
            String cat = CATEGORIES[random.nextInt(CATEGORIES.length)];
            if (!categories.contains(cat)) categories.add(cat);
        }
        analytics.put("categories", categories);
        analytics.put("tags", List.of("tag-" + random.nextInt(10), "tag-" + random.nextInt(10), "tag-" + random.nextInt(10)));
        analytics.put("conversionRate", Math.round(random.nextDouble() * 10000.0) / 100.0);
        analytics.put("returningCustomer", random.nextBoolean());
        return analytics;
    }

    private Map<String, Object> generateSettings(Random random) {
        Map<String, Object> settings = new LinkedHashMap<>();

        Map<String, Object> notifications = new LinkedHashMap<>();
        notifications.put("email", random.nextBoolean());
        notifications.put("sms", random.nextBoolean());
        notifications.put("push", random.nextBoolean());
        notifications.put("inApp", random.nextBoolean());
        settings.put("notifications", notifications);

        Map<String, Object> preferences = new LinkedHashMap<>();
        preferences.put("language", random.nextBoolean() ? "ko" : "en");
        preferences.put("timezone", "Asia/Seoul");
        preferences.put("currency", "KRW");
        preferences.put("theme", random.nextBoolean() ? "dark" : "light");
        preferences.put("fontSize", random.nextBoolean() ? "medium" : "large");
        settings.put("preferences", preferences);

        return settings;
    }

    private List<Map<String, Object>> generateLogs(Random random, LocalDateTime baseTime) {
        int logCount = 12 + random.nextInt(7); // 12~18 log entries
        List<Map<String, Object>> logs = new ArrayList<>();

        for (int i = 0; i < logCount; i++) {
            Map<String, Object> log = new LinkedHashMap<>();
            log.put("level", LOG_LEVELS[random.nextInt(LOG_LEVELS.length)]);
            log.put("message", LOG_MESSAGES[random.nextInt(LOG_MESSAGES.length)]);
            log.put("timestamp", baseTime.plusMinutes(i * 5L).format(FMT));
            log.put("traceId", UUID.randomUUID().toString().substring(0, 8));
            logs.add(log);
        }
        return logs;
    }

    private Map<String, Object> generateSystemInfo(int fileId, Random random) {
        Map<String, Object> systemInfo = new LinkedHashMap<>();
        systemInfo.put("nodeId", "node-" + (random.nextInt(5) + 1));
        systemInfo.put("region", "ap-northeast-2");
        systemInfo.put("instanceType", random.nextBoolean() ? "t3.medium" : "t3.large");
        systemInfo.put("cpuUsage", Math.round(random.nextDouble() * 10000.0) / 100.0);
        systemInfo.put("memoryUsage", Math.round(random.nextDouble() * 10000.0) / 100.0);
        systemInfo.put("diskUsage", Math.round(random.nextDouble() * 10000.0) / 100.0);
        systemInfo.put("processId", 1000 + fileId);
        return systemInfo;
    }
}
