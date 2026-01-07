package com.example.mcp.mcp_tester.testing;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestingService {
    
    private final AtomicLong testCounter = new AtomicLong(0);
    private final Map<String, Object> testStorage = new HashMap<>();
    private final List<Map<String, Object>> operationHistory = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private void logOperation(String operation, Map<String, Object> params, Map<String, Object> result) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", LocalDateTime.now().format(formatter));
        logEntry.put("operation", operation);
        logEntry.put("params", params);
        logEntry.put("success", result.containsKey("error") ? false : result.get("success"));
        operationHistory.add(logEntry);
        
        // Keep only last 100 operations
        if (operationHistory.size() > 100) {
            operationHistory.remove(0);
        }
    }
    
    private Map<String, Object> createErrorResult(String operation, String error, Object... params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", error);
        result.put("operation", operation);
        result.put("timestamp", LocalDateTime.now().format(formatter));
        if (params.length > 0) {
            result.put("invalidParams", Arrays.asList(params));
        }
        return result;
    }
    
    private Map<String, Object> createSuccessResult(String operation, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("operation", operation);
        result.put("timestamp", LocalDateTime.now().format(formatter));
        result.put("data", data);
        return result;
    }
    
    // Generate test data
    public Map<String, Object> generateTestData(String type, int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("count", count);
        
        // Input validation
        if (type == null || type.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("generateTestData", "Type cannot be null or empty", "type");
            logOperation("generateTestData", params, result);
            return result;
        }
        
        if (count <= 0 || count > 1000) {
            Map<String, Object> result = createErrorResult("generateTestData", "Count must be between 1 and 1000", "count");
            logOperation("generateTestData", params, result);
            return result;
        }
        
        try {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> items = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> item = new HashMap<>();
                switch (type.toLowerCase()) {
                    case "user":
                        item.put("id", i + 1);
                        item.put("name", "User " + (i + 1));
                        item.put("email", "user" + (i + 1) + "@example.com");
                        item.put("active", i % 2 == 0);
                        item.put("role", i % 3 == 0 ? "admin" : "user");
                        item.put("createdAt", LocalDateTime.now().minusDays(i).format(formatter));
                        break;
                    case "product":
                        item.put("id", i + 1);
                        item.put("name", "Product " + (i + 1));
                        item.put("price", Math.round(Math.random() * 1000 * 100.0) / 100.0);
                        item.put("inStock", i % 3 != 0);
                        item.put("category", "Category " + ((i % 5) + 1));
                        item.put("sku", "SKU-" + String.format("%06d", i + 1));
                        break;
                    case "string":
                        String testString = "Test string " + (i + 1);
                        item.put("value", testString);
                        item.put("length", testString.length());
                        item.put("hash", testString.hashCode());
                        break;
                    case "address":
                        item.put("id", i + 1);
                        item.put("street", (i + 1) + " Main St");
                        item.put("city", "City " + ((i % 10) + 1));
                        item.put("state", "State " + ((i % 5) + 1));
                        item.put("zipCode", String.format("%05d", 10000 + i));
                        item.put("country", "Country");
                        break;
                    default:
                        item.put("index", i);
                        item.put("value", "Generated item " + (i + 1));
                        item.put("type", type);
                }
                items.add(item);
            }
            
            data.put("type", type);
            data.put("count", count);
            data.put("items", items);
            data.put("generatedAt", LocalDateTime.now().format(formatter));
            
            Map<String, Object> result = createSuccessResult("generateTestData", data);
            logOperation("generateTestData", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = createErrorResult("generateTestData", "Generation failed: " + e.getMessage());
            logOperation("generateTestData", params, result);
            return result;
        }
    }
    
    // Validate data against pattern
    public Map<String, Object> validateData(String data, String pattern) {
        Map<String, Object> params = new HashMap<>();
        params.put("data", data);
        params.put("pattern", pattern);
        
        // Input validation
        if (data == null) {
            Map<String, Object> result = createErrorResult("validateData", "Data cannot be null", "data");
            logOperation("validateData", params, result);
            return result;
        }
        
        if (pattern == null || pattern.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("validateData", "Pattern cannot be null or empty", "pattern");
            logOperation("validateData", params, result);
            return result;
        }
        
        try {
            Pattern regex = Pattern.compile(pattern);
            boolean matches = regex.matcher(data).matches();
            
            Map<String, Object> validationResult = new HashMap<>();
            validationResult.put("valid", matches);
            validationResult.put("data", data);
            validationResult.put("pattern", pattern);
            validationResult.put("message", matches ? "Data matches pattern" : "Data does not match pattern");
            validationResult.put("dataLength", data.length());
            
            Map<String, Object> result = createSuccessResult("validateData", validationResult);
            logOperation("validateData", params, result);
            return result;
            
        } catch (PatternSyntaxException e) {
            Map<String, Object> errorResult = createErrorResult("validateData", "Invalid regex pattern: " + e.getMessage(), "pattern");
            logOperation("validateData", params, errorResult);
            return errorResult;
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("validateData", "Validation failed: " + e.getMessage());
            logOperation("validateData", params, errorResult);
            return errorResult;
        }
    }
    
    // Generate test patterns
    public Map<String, Object> generateTestPatterns(String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        
        try {
            Map<String, String> patterns = new HashMap<>();
            switch (type.toLowerCase()) {
                case "email":
                    patterns.put("basic", "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
                    patterns.put("strict", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
                    patterns.put("gmail", "^[a-zA-Z0-9._%+-]+@gmail\\.com$");
                    break;
                case "phone":
                    patterns.put("us", "^\\+1\\d{10}$");
                    patterns.put("international", "^\\+\\d{1,3}\\d{4,14}$");
                    patterns.put("general", "^\\d{10,15}$");
                    break;
                case "url":
                    patterns.put("http", "^https?://[\\w\\.-]+\\.[a-zA-Z]{2,}.*");
                    patterns.put("https", "^https://[\\w\\.-]+\\.[a-zA-Z]{2,}.*");
                    patterns.put("domain", "^[\\w\\.-]+\\.[a-zA-Z]{2,}$");
                    break;
                case "date":
                    patterns.put("iso", "^\\d{4}-\\d{2}-\\d{2}$");
                    patterns.put("us", "^\\d{2}/\\d{2}/\\d{4}$");
                    patterns.put("eu", "^\\d{2}\\.\\d{2}\\.\\d{4}$");
                    break;
                default:
                    patterns.put("alphanumeric", "^[a-zA-Z0-9]+$");
                    patterns.put("numeric", "^\\d+$");
                    patterns.put("alphabetic", "^[a-zA-Z]+$");
            }
            
            Map<String, Object> result = createSuccessResult("generateTestPatterns", patterns);
            logOperation("generateTestPatterns", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("generateTestPatterns", "Pattern generation failed: " + e.getMessage());
            logOperation("generateTestPatterns", params, errorResult);
            return errorResult;
        }
    }
    
    // Calculate test statistics
    public Map<String, Object> calculateStats(List<String> numbers) {
        Map<String, Object> params = new HashMap<>();
        params.put("numbers", numbers);
        
        // Input validation
        if (numbers == null || numbers.isEmpty()) {
            Map<String, Object> result = createErrorResult("calculateStats", "No numbers provided", "numbers");
            logOperation("calculateStats", params, result);
            return result;
        }
        
        if (numbers.size() > 10000) {
            Map<String, Object> result = createErrorResult("calculateStats", "Too many numbers (max 10000)", "numbers");
            logOperation("calculateStats", params, result);
            return result;
        }
        
        try {
            List<Double> values = new ArrayList<>();
            List<String> invalidNumbers = new ArrayList<>();
            
            for (String num : numbers) {
                try {
                    values.add(Double.parseDouble(num.trim()));
                } catch (NumberFormatException e) {
                    invalidNumbers.add(num);
                }
            }
            
            if (values.isEmpty()) {
                Map<String, Object> result = createErrorResult("calculateStats", "No valid numbers found", "numbers");
                result.put("invalidNumbers", invalidNumbers);
                logOperation("calculateStats", params, result);
                return result;
            }
            
            double sum = values.stream().mapToDouble(Double::doubleValue).sum();
            double avg = sum / values.size();
            double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double median = calculateMedian(values);
            double stdDev = calculateStandardDeviation(values, avg);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("count", values.size());
            stats.put("sum", Math.round(sum * 10000.0) / 10000.0);
            stats.put("average", Math.round(avg * 10000.0) / 10000.0);
            stats.put("min", Math.round(min * 10000.0) / 10000.0);
            stats.put("max", Math.round(max * 10000.0) / 10000.0);
            stats.put("median", Math.round(median * 10000.0) / 10000.0);
            stats.put("standardDeviation", Math.round(stdDev * 10000.0) / 10000.0);
            stats.put("values", values);
            stats.put("invalidNumbers", invalidNumbers);
            stats.put("validPercentage", Math.round((double) values.size() / numbers.size() * 10000.0) / 100.0);
            
            Map<String, Object> result = createSuccessResult("calculateStats", stats);
            logOperation("calculateStats", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("calculateStats", "Statistics calculation failed: " + e.getMessage());
            logOperation("calculateStats", params, errorResult);
            return errorResult;
        }
    }
    
    private double calculateMedian(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2;
        } else {
            return sorted.get(size / 2);
        }
    }
    
    private double calculateStandardDeviation(List<Double> values, double mean) {
        double sumSquaredDiff = values.stream()
            .mapToDouble(x -> Math.pow(x - mean, 2))
            .sum();
        return Math.sqrt(sumSquaredDiff / values.size());
    }
    
    // Generate test scenarios
    public Map<String, Object> generateTestScenarios(String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        
        try {
            List<Map<String, Object>> scenarios = new ArrayList<>();
            
            switch (category.toLowerCase()) {
                case "api":
                    scenarios.add(Map.of(
                        "name", "Success Response",
                        "type", "success",
                        "statusCode", 200,
                        "response", Map.of("message", "Operation successful", "data", "test data")
                    ));
                    scenarios.add(Map.of(
                        "name", "Not Found",
                        "type", "error",
                        "statusCode", 404,
                        "response", Map.of("error", "Resource not found")
                    ));
                    scenarios.add(Map.of(
                        "name", "Server Error",
                        "type", "error",
                        "statusCode", 500,
                        "response", Map.of("error", "Internal server error")
                    ));
                    break;
                case "form":
                    scenarios.add(Map.of(
                        "name", "Valid Form",
                        "type", "valid",
                        "data", Map.of("name", "John Doe", "email", "john@example.com", "age", 25)
                    ));
                    scenarios.add(Map.of(
                        "name", "Invalid Email",
                        "type", "invalid",
                        "data", Map.of("name", "John Doe", "email", "invalid-email", "age", 25),
                        "errors", Arrays.asList("Invalid email format")
                    ));
                    break;
                case "database":
                    scenarios.add(Map.of(
                        "name", "Successful Query",
                        "type", "success",
                        "query", "SELECT * FROM users WHERE id = 1",
                        "result", Arrays.asList(Map.of("id", 1, "name", "Test User"))
                    ));
                    scenarios.add(Map.of(
                        "name", "No Results",
                        "type", "empty",
                        "query", "SELECT * FROM users WHERE id = 999",
                        "result", Arrays.asList()
                    ));
                    break;
                default:
                    scenarios.add(Map.of(
                        "name", "Basic Test",
                        "type", "basic",
                        "input", "test input",
                        "expected", "test output"
                    ));
            }
            
            Map<String, Object> result = createSuccessResult("generateTestScenarios", scenarios);
            logOperation("generateTestScenarios", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("generateTestScenarios", "Scenario generation failed: " + e.getMessage());
            logOperation("generateTestScenarios", params, errorResult);
            return errorResult;
        }
    }
    
    // Store test data
    public Map<String, Object> storeTestData(String key, Object value) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("value", value);
        
        // Input validation
        if (key == null || key.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("storeTestData", "Key cannot be null or empty", "key");
            logOperation("storeTestData", params, result);
            return result;
        }
        
        if (testStorage.size() >= 1000) {
            Map<String, Object> result = createErrorResult("storeTestData", "Storage full (max 1000 items)");
            logOperation("storeTestData", params, result);
            return result;
        }
        
        try {
            long testId = testCounter.incrementAndGet();
            String storageKey = key + "_" + testId;
            testStorage.put(storageKey, value);
            
            Map<String, Object> data = new HashMap<>();
            data.put("key", storageKey);
            data.put("testId", testId);
            data.put("originalKey", key);
            data.put("message", "Data stored successfully");
            data.put("storageSize", testStorage.size());
            
            Map<String, Object> result = createSuccessResult("storeTestData", data);
            logOperation("storeTestData", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("storeTestData", "Storage failed: " + e.getMessage());
            logOperation("storeTestData", params, errorResult);
            return errorResult;
        }
    }
    
    // Retrieve test data
    public Map<String, Object> getTestData(String key) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        
        // Input validation
        if (key == null || key.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("getTestData", "Key cannot be null or empty", "key");
            logOperation("getTestData", params, result);
            return result;
        }
        
        try {
            if (testStorage.containsKey(key)) {
                Map<String, Object> data = new HashMap<>();
                data.put("key", key);
                data.put("value", testStorage.get(key));
                data.put("found", true);
                
                Map<String, Object> result = createSuccessResult("getTestData", data);
                logOperation("getTestData", params, result);
                return result;
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("key", key);
                data.put("found", false);
                data.put("message", "Key not found");
                data.put("availableKeys", new ArrayList<>(testStorage.keySet()));
                
                Map<String, Object> result = createSuccessResult("getTestData", data);
                logOperation("getTestData", params, result);
                return result;
            }
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("getTestData", "Retrieval failed: " + e.getMessage());
            logOperation("getTestData", params, errorResult);
            return errorResult;
        }
    }
    
    // List all stored test data
    public Map<String, Object> listTestData() {
        Map<String, Object> params = new HashMap<>();
        
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("totalKeys", testStorage.size());
            data.put("keys", new ArrayList<>(testStorage.keySet()));
            data.put("storageSize", testStorage.size());
            data.put("maxStorageSize", 1000);
            data.put("storagePercentage", Math.round((double) testStorage.size() / 1000 * 10000.0) / 100.0);
            
            // Include preview of stored data (limit for performance)
            Map<String, Object> preview = new HashMap<>();
            int count = 0;
            for (Map.Entry<String, Object> entry : testStorage.entrySet()) {
                if (count >= 10) break;
                Object value = entry.getValue();
                if (value != null && value.toString().length() > 100) {
                    preview.put(entry.getKey(), value.toString().substring(0, 100) + "...");
                } else {
                    preview.put(entry.getKey(), value);
                }
                count++;
            }
            data.put("preview", preview);
            
            Map<String, Object> result = createSuccessResult("listTestData", data);
            logOperation("listTestData", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("listTestData", "Listing failed: " + e.getMessage());
            logOperation("listTestData", params, errorResult);
            return errorResult;
        }
    }
    
    // Clear test data
    public Map<String, Object> clearTestData() {
        Map<String, Object> params = new HashMap<>();
        
        try {
            int count = testStorage.size();
            testStorage.clear();
            testCounter.set(0);
            
            Map<String, Object> data = new HashMap<>();
            data.put("clearedKeys", count);
            data.put("message", "Test data cleared successfully");
            data.put("storageSize", 0);
            
            Map<String, Object> result = createSuccessResult("clearTestData", data);
            logOperation("clearTestData", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("clearTestData", "Clear failed: " + e.getMessage());
            logOperation("clearTestData", params, errorResult);
            return errorResult;
        }
    }
    
    // Get operation history
    public Map<String, Object> getOperationHistory() {
        Map<String, Object> params = new HashMap<>();
        
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("totalOperations", operationHistory.size());
            data.put("history", operationHistory);
            data.put("maxHistorySize", 100);
            
            Map<String, Object> result = createSuccessResult("getOperationHistory", data);
            logOperation("getOperationHistory", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("getOperationHistory", "History retrieval failed: " + e.getMessage());
            logOperation("getOperationHistory", params, errorResult);
            return errorResult;
        }
    }
    
    // Generate random strings with enhanced options
    public Map<String, Object> generateRandomStrings(int count, int length, String charset) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("length", length);
        params.put("charset", charset);
        
        // Input validation
        if (count <= 0 || count > 1000) {
            Map<String, Object> result = createErrorResult("generateRandomStrings", "Count must be between 1 and 1000", "count");
            logOperation("generateRandomStrings", params, result);
            return result;
        }
        
        if (length <= 0 || length > 1000) {
            Map<String, Object> result = createErrorResult("generateRandomStrings", "Length must be between 1 and 1000", "length");
            logOperation("generateRandomStrings", params, result);
            return result;
        }
        
        try {
            String chars = charset != null ? charset : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            List<String> strings = new ArrayList<>();
            List<Map<String, Object>> metadata = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    int index = (int) (Math.random() * chars.length());
                    sb.append(chars.charAt(index));
                }
                String str = sb.toString();
                strings.add(str);
                
                Map<String, Object> meta = new HashMap<>();
                meta.put("index", i);
                meta.put("length", str.length());
                meta.put("hash", str.hashCode());
                metadata.add(meta);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            data.put("length", length);
            data.put("charset", chars);
            data.put("strings", strings);
            data.put("metadata", metadata);
            data.put("generatedAt", LocalDateTime.now().format(formatter));
            
            Map<String, Object> result = createSuccessResult("generateRandomStrings", data);
            logOperation("generateRandomStrings", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("generateRandomStrings", "Generation failed: " + e.getMessage());
            logOperation("generateRandomStrings", params, errorResult);
            return errorResult;
        }
    }
    
    // Performance test utility
    public Map<String, Object> performanceTest(String operation, int iterations) {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", operation);
        params.put("iterations", iterations);
        
        // Input validation
        if (iterations <= 0 || iterations > 100000) {
            Map<String, Object> result = createErrorResult("performanceTest", "Iterations must be between 1 and 100000", "iterations");
            logOperation("performanceTest", params, result);
            return result;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            switch (operation.toLowerCase()) {
                case "string_concat":
                    for (int i = 0; i < iterations; i++) {
                        String result = "test" + i + "data";
                    }
                    break;
                case "math_calculation":
                    for (int i = 0; i < iterations; i++) {
                        double result = Math.sin(i) * Math.cos(i) + Math.sqrt(i + 1);
                    }
                    break;
                case "hash_calculation":
                    for (int i = 0; i < iterations; i++) {
                        int hash = ("test_string_" + i).hashCode();
                    }
                    break;
                default:
                    for (int i = 0; i < iterations; i++) {
                        // Simple loop
                        int temp = i * 2;
                    }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            Map<String, Object> data = new HashMap<>();
            data.put("operation", operation);
            data.put("iterations", iterations);
            data.put("durationMs", duration);
            data.put("operationsPerSecond", Math.round((double) iterations / duration * 1000 * 100.0) / 100.0);
            data.put("averageTimePerOperation", Math.round((double) duration / iterations * 10000.0) / 10000.0);
            data.put("startTime", startTime);
            data.put("endTime", endTime);
            
            Map<String, Object> result = createSuccessResult("performanceTest", data);
            logOperation("performanceTest", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("performanceTest", "Performance test failed: " + e.getMessage());
            logOperation("performanceTest", params, errorResult);
            return errorResult;
        }
    }
}
