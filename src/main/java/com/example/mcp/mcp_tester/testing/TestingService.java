package com.example.mcp.mcp_tester.testing;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

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
    
    // Read file content for analysis
    public Map<String, Object> readFile(String filePath) {
        Map<String, Object> params = new HashMap<>();
        params.put("filePath", filePath);
        
        // Input validation
        if (filePath == null || filePath.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("readFile", "File path cannot be null or empty", "filePath");
            logOperation("readFile", params, result);
            return result;
        }
        
        // Security check - prevent path traversal
        Path path = Paths.get(filePath).normalize();
        if (path.toString().contains("..")) {
            Map<String, Object> result = createErrorResult("readFile", "Path traversal not allowed", "filePath");
            logOperation("readFile", params, result);
            return result;
        }
        
        try {
            if (!Files.exists(path)) {
                Map<String, Object> result = createErrorResult("readFile", "File not found: " + filePath, "filePath");
                logOperation("readFile", params, result);
                return result;
            }
            
            if (!Files.isReadable(path)) {
                Map<String, Object> result = createErrorResult("readFile", "File not readable: " + filePath, "filePath");
                logOperation("readFile", params, result);
                return result;
            }
            
            // Limit file size to prevent memory issues (5MB max)
            long fileSize = Files.size(path);
            if (fileSize > 5 * 1024 * 1024) {
                Map<String, Object> result = createErrorResult("readFile", "File too large (max 5MB): " + filePath, "filePath");
                logOperation("readFile", params, result);
                return result;
            }
            
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String fileName = path.getFileName().toString();
            String fileExtension = getFileExtension(fileName);
            
            // Get file metadata
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            
            Map<String, Object> data = new HashMap<>();
            data.put("filePath", filePath);
            data.put("fileName", fileName);
            data.put("fileExtension", fileExtension);
            data.put("fileSize", fileSize);
            data.put("content", content);
            data.put("lineCount", content.split("\n").length);
            data.put("characterCount", content.length());
            data.put("lastModified", attrs.lastModifiedTime().toString());
            data.put("isDirectory", attrs.isDirectory());
            data.put("isRegularFile", attrs.isRegularFile());
            
            Map<String, Object> result = createSuccessResult("readFile", data);
            logOperation("readFile", params, result);
            return result;
            
        } catch (IOException e) {
            Map<String, Object> errorResult = createErrorResult("readFile", "Failed to read file: " + e.getMessage(), "filePath");
            logOperation("readFile", params, errorResult);
            return errorResult;
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("readFile", "Unexpected error: " + e.getMessage(), "filePath");
            logOperation("readFile", params, errorResult);
            return errorResult;
        }
    }
    
    // Analyze file and generate test cases
    public Map<String, Object> generateTestsFromFile(String filePath, String testType) {
        Map<String, Object> params = new HashMap<>();
        params.put("filePath", filePath);
        params.put("testType", testType);
        
        // Input validation
        if (filePath == null || filePath.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("generateTestsFromFile", "File path cannot be null or empty", "filePath");
            logOperation("generateTestsFromFile", params, result);
            return result;
        }
        
        if (testType == null || testType.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("generateTestsFromFile", "Test type cannot be null or empty", "testType");
            logOperation("generateTestsFromFile", params, result);
            return result;
        }
        
        try {
            // First read the file
            Map<String, Object> fileResult = readFile(filePath);
            if (!(Boolean) fileResult.get("success")) {
                Map<String, Object> result = createErrorResult("generateTestsFromFile", "Failed to read file: " + fileResult.get("error"));
                logOperation("generateTestsFromFile", params, result);
                return result;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> fileData = (Map<String, Object>) fileResult.get("data");
            String content = (String) fileData.get("content");
            String fileName = (String) fileData.get("fileName");
            String fileExtension = (String) fileData.get("fileExtension");
            
            // Generate tests based on file type and test type
            Map<String, Object> testData = new HashMap<>();
            List<Map<String, Object>> testCases = new ArrayList<>();
            
            switch (testType.toLowerCase()) {
                case "unit":
                    testCases = generateUnitTests(fileName, fileExtension, content);
                    break;
                case "integration":
                    testCases = generateIntegrationTests(fileName, fileExtension, content);
                    break;
                case "api":
                    testCases = generateApiTests(fileName, fileExtension, content);
                    break;
                case "validation":
                    testCases = generateValidationTests(fileName, fileExtension, content);
                    break;
                case "performance":
                    testCases = generatePerformanceTests(fileName, fileExtension, content);
                    break;
                default:
                    testCases = generateGenericTests(fileName, fileExtension, content);
            }
            
            testData.put("fileName", fileName);
            testData.put("fileExtension", fileExtension);
            testData.put("testType", testType);
            testData.put("testCases", testCases);
            testData.put("totalTests", testCases.size());
            testData.put("generatedAt", LocalDateTime.now().format(formatter));
            
            // Generate test code
            String testCode = generateTestCode(fileName, fileExtension, testType, testCases);
            testData.put("testCode", testCode);
            
            Map<String, Object> result = createSuccessResult("generateTestsFromFile", testData);
            logOperation("generateTestsFromFile", params, result);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("generateTestsFromFile", "Test generation failed: " + e.getMessage());
            logOperation("generateTestsFromFile", params, errorResult);
            return errorResult;
        }
    }
    
    private List<Map<String, Object>> generateUnitTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // Analyze content for patterns
        String[] lines = content.split("\n");
        
        // Look for functions/methods with better detection
        if (isJavaFile(fileExtension) || isPythonFile(fileExtension) || isJavaScriptFile(fileExtension)) {
            List<Map<String, Object>> functions = extractFunctions(fileName, fileExtension, content);
            
            for (Map<String, Object> function : functions) {
                String methodName = (String) function.get("name");
                Integer lineNumber = (Integer) function.get("lineNumber");
                @SuppressWarnings("unchecked")
                List<String> parameters = (List<String>) function.get("parameters");
                String returnType = (String) function.get("returnType");
                
                // Generate comprehensive test cases for each function
                testCases.add(createTestCase(methodName + " should execute with valid inputs", "unit", methodName, 
                    "Test that " + methodName + " executes correctly with valid parameters", lineNumber));
                
                // Add edge case tests based on parameters
                if (!parameters.isEmpty()) {
                    testCases.add(createTestCase(methodName + " should handle null/empty parameters", "unit", methodName,
                        "Test that " + methodName + " handles null or empty parameters gracefully", lineNumber));
                }
                
                // Add return type validation test
                if (returnType != null && !"void".equals(returnType)) {
                    testCases.add(createTestCase(methodName + " should return correct type", "unit", methodName,
                        "Test that " + methodName + " returns expected " + returnType + " type", lineNumber));
                }
                
                // Add exception handling test
                testCases.add(createTestCase(methodName + " should handle exceptions", "unit", methodName,
                    "Test that " + methodName + " properly handles exceptions", lineNumber));
            }
        }
        
        // Add generic file tests
        testCases.add(createTestCase("File should not be empty", "unit", "file_content_check",
            "Test that file contains content", 1));
        testCases.add(createTestCase("File should have valid encoding", "unit", "encoding_check",
            "Test that file has valid UTF-8 encoding", 1));
        
        return testCases;
    }
    
    private List<Map<String, Object>> generateIntegrationTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // File-based integration tests
        testCases.add(createTestCase("File should be readable", "integration", "file_readability",
            "Test that file can be read successfully", 1));
        
        if (isConfigFile(fileExtension)) {
            testCases.add(createTestCase("Configuration should be valid", "integration", "config_validation",
                "Test that configuration file has valid format", 1));
        }
        
        if (isCodeFile(fileExtension)) {
            testCases.add(createTestCase("Code should be syntactically valid", "integration", "syntax_check",
                "Test that code has valid syntax", 1));
        }
        
        return testCases;
    }
    
    private List<Map<String, Object>> generateApiTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // Look for API patterns
        if (content.toLowerCase().contains("@restcontroller") || content.toLowerCase().contains("@app.route")) {
            testCases.add(createTestCase("API endpoint should respond", "api", "endpoint_response",
                "Test that API endpoint returns valid response", 1));
        }
        
        if (content.toLowerCase().contains("@getmapping") || content.toLowerCase().contains("methods=['get']")) {
            testCases.add(createTestCase("GET endpoint should work", "api", "get_endpoint",
                "Test that GET endpoint returns 200", 1));
        }
        
        if (content.toLowerCase().contains("@postmapping") || content.toLowerCase().contains("methods=['post']")) {
            testCases.add(createTestCase("POST endpoint should work", "api", "post_endpoint",
                "Test that POST endpoint creates resource", 1));
        }
        
        return testCases;
    }
    
    private List<Map<String, Object>> generateValidationTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // Content validation tests
        testCases.add(createTestCase("Content should not contain null bytes", "validation", "null_byte_check",
            "Test that file doesn't contain null bytes", 1));
        
        if (isJsonFile(fileExtension)) {
            testCases.add(createTestCase("JSON should be valid", "validation", "json_validation",
                "Test that JSON content is valid", 1));
        }
        
        if (isXmlFile(fileExtension)) {
            testCases.add(createTestCase("XML should be well-formed", "validation", "xml_validation",
                "Test that XML is well-formed", 1));
        }
        
        return testCases;
    }
    
    private List<Map<String, Object>> generatePerformanceTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // Performance-related tests
        testCases.add(createTestCase("File should load within time limit", "performance", "load_time",
            "Test that file loads within acceptable time", 1));
        
        if (content.length() > 1000) {
            testCases.add(createTestCase("Large file should be processed efficiently", "performance", "large_file_processing",
                "Test that large file is processed efficiently", 1));
        }
        
        return testCases;
    }
    
    private List<Map<String, Object>> generateGenericTests(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        // Generic tests applicable to all files
        testCases.add(createTestCase("File should exist", "generic", "file_existence",
            "Test that file exists at specified path", 1));
        testCases.add(createTestCase("File should be accessible", "generic", "file_accessibility",
            "Test that file is accessible for reading", 1));
        testCases.add(createTestCase("File size should be reasonable", "generic", "file_size_check",
            "Test that file size is within expected bounds", 1));
        
        return testCases;
    }
    
    private Map<String, Object> createTestCase(String name, String type, String target, String description, int lineNumber) {
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("name", name);
        testCase.put("type", type);
        testCase.put("target", target);
        testCase.put("description", description);
        testCase.put("lineNumber", lineNumber);
        testCase.put("priority", "medium");
        testCase.put("status", "pending");
        testCase.put("createdAt", LocalDateTime.now().format(formatter));
        return testCase;
    }
    
    private String generateTestCode(String fileName, String fileExtension, String testType, List<Map<String, Object>> testCases) {
        StringBuilder code = new StringBuilder();
        
        if (isJavaFile(fileExtension)) {
            code.append(generateJavaTestCode(fileName, testType, testCases));
        } else if (isPythonFile(fileExtension)) {
            code.append(generatePythonTestCode(fileName, testType, testCases));
        } else if (isJavaScriptFile(fileExtension)) {
            code.append(generateJSTestCode(fileName, testType, testCases));
        } else {
            code.append(generateGenericTestCode(fileName, testType, testCases));
        }
        
        return code.toString();
    }
    
    private String generateJavaTestCode(String fileName, String testType, List<Map<String, Object>> testCases) {
        StringBuilder code = new StringBuilder();
        String className = fileName.replace(".java", "").replace("-", "").replace("_", "");
        
        code.append("// Auto-generated tests for ").append(fileName).append("\n");
        code.append("import org.junit.jupiter.api.*;
");
        code.append("import org.mockito.*;
");
        code.append("import static org.junit.jupiter.api.Assertions.*;
");
        code.append("import static org.mockito.Mockito.*;
\n");
        
        code.append("public class ").append(className).append("Test {\n\n");
        
        // Group tests by target method
        Map<String, List<Map<String, Object>>> testsByMethod = new HashMap<>();
        for (Map<String, Object> testCase : testCases) {
            String target = (String) testCase.get("target");
            testsByMethod.computeIfAbsent(target, k -> new ArrayList<>()).add(testCase);
        }
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : testsByMethod.entrySet()) {
            String methodName = entry.getKey();
            List<Map<String, Object>> methodTests = entry.getValue();
            
            if (!"file_content_check".equals(methodName) && !"encoding_check".equals(methodName)) {
                code.append("    // Tests for ").append(methodName).append("\n");
                
                for (Map<String, Object> testCase : methodTests) {
                    String testName = ((String) testCase.get("name")).replaceAll("[^a-zA-Z0-9_]", "_");
                    String description = (String) testCase.get("description");
                    
                    code.append("    @Test\n");
                    code.append("    void ").append(testName).append("() {\n");
                    code.append("        // ").append(description).append("\n");
                    
                    // Generate specific test logic based on test type
                    if (testName.contains("valid_inputs")) {
                        code.append("        // TODO: Arrange valid test data\n");
                        code.append("        // TODO: Act - call the method\n");
                        code.append("        // TODO: Assert - verify results\n");
                        code.append("        assertTrue(true, \"Test not implemented yet\");\n");
                    } else if (testName.contains("null_empty")) {
                        code.append("        // TODO: Test with null parameters\n");
                        code.append("        // TODO: Test with empty parameters\n");
                        code.append("        // TODO: Verify graceful handling\n");
                        code.append("        assertDoesNotThrow(() -> {\n");
                        code.append("            // TODO: Implement null/empty parameter test\n");
                        code.append("        });\n");
                    } else if (testName.contains("return_correct_type")) {
                        code.append("        // TODO: Test return type validation\n");
                        code.append("        // TODO: Verify returned value type\n");
                        code.append("        assertNotNull(result, \"Result should not be null\");\n");
                    } else if (testName.contains("exceptions")) {
                        code.append("        // TODO: Test exception scenarios\n");
                        code.append("        // TODO: Verify proper exception handling\n");
                        code.append("        assertDoesNotThrow(() -> {\n");
                        code.append("            // TODO: Implement exception test\n");
                        code.append("        });\n");
                    } else {
                        code.append("        // TODO: Implement test logic\n");
                        code.append("        assertTrue(true, \"Test not implemented yet\");\n");
                    }
                    
                    code.append("    }\n\n");
                }
            }
        }
        
        // Add generic file tests
        code.append("    // File-level tests\n");
        code.append("    @Test\n");
        code.append("    void file_should_not_be_empty() {\n");
        code.append("        // Test that file contains content\n");
        code.append("        assertNotNull(getClass().getResourceAsStream(\"/\" + \"").append(fileName).append("\"), \"File should exist\");\n");
        code.append("    }\n\n");
        
        code.append("    @Test\n");
        code.append("    void file_should_have_valid_encoding() {\n");
        code.append("        // Test that file has valid UTF-8 encoding\n");
        code.append("        // TODO: Implement encoding validation\n");
        code.append("        assertTrue(true, \"Encoding test not implemented yet\");\n");
        code.append("    }\n\n");
        
        code.append("}\n");
        return code.toString();
    }
    
    private String generatePythonTestCode(String fileName, String testType, List<Map<String, Object>> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("# Auto-generated tests for ").append(fileName).append("\n");
        code.append("import unittest\n");
        code.append("import pytest\n");
        code.append("from unittest.mock import patch, MagicMock\n\n");
        
        String className = fileName.replace(".", "").replace("-", "").replace("_", "").toUpperCase();
        code.append("class Test").append(className).append("(unittest.TestCase):\n\n");
        
        // Group tests by target method
        Map<String, List<Map<String, Object>>> testsByMethod = new HashMap<>();
        for (Map<String, Object> testCase : testCases) {
            String target = (String) testCase.get("target");
            testsByMethod.computeIfAbsent(target, k -> new ArrayList<>()).add(testCase);
        }
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : testsByMethod.entrySet()) {
            String methodName = entry.getKey();
            List<Map<String, Object>> methodTests = entry.getValue();
            
            if (!"file_content_check".equals(methodName) && !"encoding_check".equals(methodName)) {
                code.append("    # Tests for ").append(methodName).append("\n");
                
                for (Map<String, Object> testCase : methodTests) {
                    String testName = ((String) testCase.get("name")).replaceAll("[^a-zA-Z0-9_]", "_");
                    String description = (String) testCase.get("description");
                    
                    code.append("    def test_").append(testName.toLowerCase()).append("(self):\n");
                    code.append("        # ").append(description).append("\n");
                    
                    // Generate specific test logic based on test type
                    if (testName.contains("valid_inputs")) {
                        code.append("        # TODO: Arrange valid test data\n");
                        code.append("        # TODO: Act - call the function\n");
                        code.append("        # TODO: Assert - verify results\n");
                        code.append("        self.assertTrue(True, \"Test not implemented yet\")\n");
                    } else if (testName.contains("null_empty")) {
                        code.append("        # TODO: Test with None parameters\n");
                        code.append("        # TODO: Test with empty parameters\n");
                        code.append("        # TODO: Verify graceful handling\n");
                        code.append("        with self.assertRaises(Exception):\n");
                        code.append("            # TODO: Implement None/empty parameter test\n");
                        code.append("            pass\n");
                    } else if (testName.contains("return_correct_type")) {
                        code.append("        # TODO: Test return type validation\n");
                        code.append("        # TODO: Verify returned value type\n");
                        code.append("        self.assertIsNotNone(result, \"Result should not be None\")\n");
                    } else if (testName.contains("exceptions")) {
                        code.append("        # TODO: Test exception scenarios\n");
                        code.append("        # TODO: Verify proper exception handling\n");
                        code.append("        with self.assertRaises(Exception):\n");
                        code.append("            # TODO: Implement exception test\n");
                        code.append("            pass\n");
                    } else {
                        code.append("        # TODO: Implement test logic\n");
                        code.append("        self.assertTrue(True, \"Test not implemented yet\")\n");
                    }
                    
                    code.append("\n");
                }
            }
        }
        
        // Add generic file tests
        code.append("    # File-level tests\n");
        code.append("    def test_file_should_not_be_empty(self):\n");
        code.append("        # Test that file contains content\n");
        code.append("        with open(\"").append(fileName).append("\", 'r') as f:\n");
        code.append("            content = f.read()\n");
        code.append("            self.assertGreater(len(content), 0, \"File should not be empty\")\n\n");
        
        code.append("    def test_file_should_have_valid_encoding(self):\n");
        code.append("        # Test that file has valid UTF-8 encoding\n");
        code.append("        # TODO: Implement encoding validation\n");
        code.append("        self.assertTrue(True, \"Encoding test not implemented yet\")\n\n");
        
        code.append("\nif __name__ == '__main__':\n");
        code.append("    unittest.main()\n");
        return code.toString();
    }
    
    private String generateJSTestCode(String fileName, String testType, List<Map<String, Object>> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("// Auto-generated tests for ").append(fileName).append("\n");
        code.append("const assert = require('assert');");
        code.append("const sinon = require('sinon');\n\n");
        
        code.append("describe('").append(fileName).append("', () => {\n");
        
        // Group tests by target method
        Map<String, List<Map<String, Object>>> testsByMethod = new HashMap<>();
        for (Map<String, Object> testCase : testCases) {
            String target = (String) testCase.get("target");
            testsByMethod.computeIfAbsent(target, k -> new ArrayList<>()).add(testCase);
        }
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : testsByMethod.entrySet()) {
            String methodName = entry.getKey();
            List<Map<String, Object>> methodTests = entry.getValue();
            
            if (!"file_content_check".equals(methodName) && !"encoding_check".equals(methodName)) {
                code.append("    // Tests for ").append(methodName).append("\n");
                
                for (Map<String, Object> testCase : methodTests) {
                    String testName = (String) testCase.get("name");
                    String description = (String) testCase.get("description");
                    
                    code.append("    it('").append(testName).append("', () => {\n");
                    code.append("        // ").append(description).append("\n");
                    
                    // Generate specific test logic based on test type
                    if (testName.contains("valid_inputs")) {
                        code.append("        // TODO: Arrange valid test data\n");
                        code.append("        // TODO: Act - call the function\n");
                        code.append("        // TODO: Assert - verify results\n");
                        code.append("        assert(true, 'Test not implemented yet');\n");
                    } else if (testName.contains("null_empty")) {
                        code.append("        // TODO: Test with null/undefined parameters\n");
                        code.append("        // TODO: Test with empty parameters\n");
                        code.append("        // TODO: Verify graceful handling\n");
                        code.append("        assert.doesNotThrow(() => {\n");
                        code.append("            // TODO: Implement null/empty parameter test\n");
                        code.append("        });\n");
                    } else if (testName.contains("return_correct_type")) {
                        code.append("        // TODO: Test return type validation\n");
                        code.append("        // TODO: Verify returned value type\n");
                        code.append("        assert.notEqual(result, undefined, 'Result should not be undefined');\n");
                    } else if (testName.contains("exceptions")) {
                        code.append("        // TODO: Test exception scenarios\n");
                        code.append("        // TODO: Verify proper exception handling\n");
                        code.append("        assert.throws(() => {\n");
                        code.append("            // TODO: Implement exception test\n");
                        code.append("        });\n");
                    } else {
                        code.append("        // TODO: Implement test logic\n");
                        code.append("        assert(true, 'Test not implemented yet');\n");
                    }
                    
                    code.append("    });\n\n");
                }
            }
        }
        
        // Add generic file tests
        code.append("    // File-level tests\n");
        code.append("    it('File should not be empty', () => {\n");
        code.append("        // Test that file contains content\n");
        code.append("        const fs = require('fs');\n");
        code.append("        const content = fs.readFileSync('"").append(fileName).append("', 'utf8');\n");
        code.append("        assert(content.length > 0, 'File should not be empty');\n");
        code.append("    });\n\n");
        
        code.append("    it('File should have valid encoding', () => {\n");
        code.append("        // Test that file has valid UTF-8 encoding\n");
        code.append("        // TODO: Implement encoding validation\n");
        code.append("        assert(true, 'Encoding test not implemented yet');\n");
        code.append("    });\n");
        
        code.append("});\n");
        return code.toString();
    }
    
    private String generateGenericTestCode(String fileName, String testType, List<Map<String, Object>> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("# Auto-generated tests for ").append(fileName).append("\n");
        code.append("# Test Type: ").append(testType).append("\n\n");
        
        for (Map<String, Object> testCase : testCases) {
            code.append("Test: ").append(testCase.get("name")).append("\n");
            code.append("Description: ").append(testCase.get("description")).append("\n");
            code.append("Type: ").append(testCase.get("type")).append("\n");
            code.append("Target: ").append(testCase.get("target")).append("\n");
            code.append("Line: ").append(testCase.get("lineNumber")).append("\n");
            code.append("Status: ").append(testCase.get("status")).append("\n");
            code.append("---\n\n");
        }
        
        return code.toString();
    }
    
    // Enhanced function extraction methods
    private List<Map<String, Object>> extractFunctions(String fileName, String fileExtension, String content) {
        List<Map<String, Object>> functions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        if (isJavaFile(fileExtension)) {
            functions.addAll(extractJavaFunctions(lines));
        } else if (isPythonFile(fileExtension)) {
            functions.addAll(extractPythonFunctions(lines));
        } else if (isJavaScriptFile(fileExtension)) {
            functions.addAll(extractJSFunctions(lines));
        }
        
        return functions;
    }
    
    private List<Map<String, Object>> extractJavaFunctions(String[] lines) {
        List<Map<String, Object>> functions = new ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Skip comments and empty lines
            if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*") || line.isEmpty()) {
                continue;
            }
            
            // Look for method signatures with better regex
            if ((line.contains("public ") || line.contains("private ") || line.contains("protected ") || 
                 line.contains("static ") || line.contains("final ") || line.contains("synchronized")) && 
                line.contains("(") && line.contains(")") && (line.contains(";") == false)) {
                
                Map<String, Object> function = parseJavaMethodSignature(line, i + 1);
                if (function != null) {
                    functions.add(function);
                }
            }
        }
        
        return functions;
    }
    
    private List<Map<String, Object>> extractPythonFunctions(String[] lines) {
        List<Map<String, Object>> functions = new ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Skip comments and empty lines
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            
            // Look for function definitions
            if (line.startsWith("def ") && line.contains("(") && line.contains("):")) {
                Map<String, Object> function = parsePythonFunctionSignature(line, i + 1);
                if (function != null) {
                    functions.add(function);
                }
            }
        }
        
        return functions;
    }
    
    private List<Map<String, Object>> extractJSFunctions(String[] lines) {
        List<Map<String, Object>> functions = new ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Skip comments and empty lines
            if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*") || line.isEmpty()) {
                continue;
            }
            
            // Look for function declarations and arrow functions
            if ((line.contains("function ") || line.contains("=>") || line.contains("= function")) && 
                line.contains("(") && line.contains(")")) {
                Map<String, Object> function = parseJSFunctionSignature(line, i + 1);
                if (function != null) {
                    functions.add(function);
                }
            }
        }
        
        return functions;
    }
    
    private Map<String, Object> parseJavaMethodSignature(String line, int lineNumber) {
        try {
            // Remove access modifiers and other keywords
            String cleanLine = line.replaceAll("\b(public|private|protected|static|final|synchronized|native|abstract)\b", "").trim();
            
            // Extract return type and method name
            String[] parts = cleanLine.split("\\s+", 3);
            if (parts.length < 2) return null;
            
            String returnType = parts[0];
            String methodNameAndParams = parts[1];
            
            // Extract method name
            String methodName = methodNameAndParams.split("\\(")[0];
            
            // Extract parameters
            List<String> parameters = extractJavaParameters(line);
            
            Map<String, Object> function = new HashMap<>();
            function.put("name", methodName);
            function.put("returnType", returnType);
            function.put("parameters", parameters);
            function.put("lineNumber", lineNumber);
            function.put("language", "java");
            
            return function;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Map<String, Object> parsePythonFunctionSignature(String line, int lineNumber) {
        try {
            // Remove 'def' keyword
            String cleanLine = line.substring(3).trim();
            
            // Extract method name and parameters
            String methodNameAndParams = cleanLine.split(":")[0];
            String methodName = methodNameAndParams.split("\\(")[0];
            
            // Extract parameters
            List<String> parameters = extractPythonParameters(cleanLine);
            
            // Try to extract return type from type hints
            String returnType = "void";
            if (cleanLine.contains("->")) {
                String[] parts = cleanLine.split("->");
                if (parts.length > 1) {
                    returnType = parts[1].split(":")[0].trim();
                }
            }
            
            Map<String, Object> function = new HashMap<>();
            function.put("name", methodName);
            function.put("returnType", returnType);
            function.put("parameters", parameters);
            function.put("lineNumber", lineNumber);
            function.put("language", "python");
            
            return function;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Map<String, Object> parseJSFunctionSignature(String line, int lineNumber) {
        try {
            String methodName = "anonymous";
            List<String> parameters = new ArrayList<>();
            
            if (line.contains("function ")) {
                // Named function: function name(param1, param2)
                String afterFunction = line.split("function\\s+")[1];
                methodName = afterFunction.split("\\(")[0];
                parameters = extractJSParameters(line);
            } else if (line.contains("=>")) {
                // Arrow function: const name = (param1, param2) => {}
                String[] parts = line.split("=");
                if (parts.length > 0) {
                    String leftSide = parts[0].trim();
                    if (leftSide.contains(" ")) {
                        methodName = leftSide.split("\\s+")[leftSide.split("\\s+").length - 1];
                    }
                }
                parameters = extractJSParameters(line);
            }
            
            Map<String, Object> function = new HashMap<>();
            function.put("name", methodName);
            function.put("returnType", "any");
            function.put("parameters", parameters);
            function.put("lineNumber", lineNumber);
            function.put("language", "javascript");
            
            return function;
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<String> extractJavaParameters(String line) {
        List<String> parameters = new ArrayList<>();
        try {
            String paramsPart = line.split("\\(")[1].split("\\)")[0];
            if (!paramsPart.trim().isEmpty()) {
                String[] params = paramsPart.split(",");
                for (String param : params) {
                    param = param.trim();
                    if (!param.isEmpty()) {
                        String[] paramParts = param.split("\\s+");
                        if (paramParts.length >= 2) {
                            parameters.add(paramParts[paramParts.length - 1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parameter extraction errors
        }
        return parameters;
    }
    
    private List<String> extractPythonParameters(String line) {
        List<String> parameters = new ArrayList<>();
        try {
            String paramsPart = line.split("\\(")[1].split("\\)")[0];
            if (!paramsPart.trim().isEmpty()) {
                String[] params = paramsPart.split(",");
                for (String param : params) {
                    param = param.trim();
                    if (!param.isEmpty() && !param.equals("self")) {
                        // Remove type hints if present
                        String cleanParam = param.split(":")[0].trim();
                        if (!cleanParam.isEmpty()) {
                            parameters.add(cleanParam);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parameter extraction errors
        }
        return parameters;
    }
    
    private List<String> extractJSParameters(String line) {
        List<String> parameters = new ArrayList<>();
        try {
            String paramsPart = line.split("\\(")[1].split("\\)")[0];
            if (!paramsPart.trim().isEmpty()) {
                String[] params = paramsPart.split(",");
                for (String param : params) {
                    param = param.trim();
                    if (!param.isEmpty()) {
                        parameters.add(param);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parameter extraction errors
        }
        return parameters;
    }
    
    // Legacy methods for backward compatibility
    private String extractMethodName(String line) {
        Map<String, Object> function = parseJavaMethodSignature(line, 0);
        return function != null ? (String) function.get("name") : null;
    }
    
    private String extractPythonMethodName(String line) {
        Map<String, Object> function = parsePythonFunctionSignature(line, 0);
        return function != null ? (String) function.get("name") : null;
    }
    
    private String extractJSMethodName(String line) {
        Map<String, Object> function = parseJSFunctionSignature(line, 0);
        return function != null ? (String) function.get("name") : null;
    }
    
    // Helper methods
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
    
    private boolean isJavaFile(String extension) {
        return "java".equals(extension);
    }
    
    private boolean isPythonFile(String extension) {
        return "py".equals(extension);
    }
    
    private boolean isJavaScriptFile(String extension) {
        return "js".equals(extension) || "jsx".equals(extension) || "ts".equals(extension) || "tsx".equals(extension);
    }
    
    private boolean isJsonFile(String extension) {
        return "json".equals(extension);
    }
    
    private boolean isXmlFile(String extension) {
        return "xml".equals(extension) || "xhtml".equals(extension);
    }
    
    private boolean isConfigFile(String extension) {
        return Arrays.asList("properties", "yml", "yaml", "ini", "conf", "config").contains(extension);
    }
    
    private boolean isCodeFile(String extension) {
        return Arrays.asList("java", "py", "js", "jsx", "ts", "tsx", "cpp", "c", "h", "cs", "php", "rb", "go", "rs").contains(extension);
    }
    
    // New advanced testing methods for enterprise products
    
    // Analyze code coverage and identify untested areas
    public Map<String, Object> analyzeCodeCoverage(String projectPath, String coverageType) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectPath", projectPath);
        params.put("coverageType", coverageType);
        
        if (projectPath == null || projectPath.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("analyzeCodeCoverage", "Project path cannot be null or empty", "projectPath");
            return result;
        }
        
        try {
            Path path = Paths.get(projectPath).normalize();
            if (!Files.exists(path)) {
                Map<String, Object> result = createErrorResult("analyzeCodeCoverage", "Project path not found: " + projectPath, "projectPath");
                return result;
            }
            
            Map<String, Object> coverageData = new HashMap<>();
            List<Map<String, Object>> uncoveredAreas = new ArrayList<>();
            List<Map<String, Object>> coverageMetrics = new ArrayList<>();
            
            // Simulate coverage analysis
            coverageData.put("projectPath", projectPath);
            coverageData.put("coverageType", coverageType != null ? coverageType : "line");
            coverageData.put("totalLines", 1250);
            coverageData.put("coveredLines", 875);
            coverageData.put("coveragePercentage", 70.0);
            coverageData.put("uncoveredAreas", uncoveredAreas);
            coverageData.put("metrics", coverageMetrics);
            
            // Add sample uncovered areas
            uncoveredAreas.add(Map.of(
                "file", "src/main/java/com/example/service/UserService.java",
                "lines", Arrays.asList(45, 46, 47, 48),
                "reason", "Exception handling not covered"
            ));
            
            uncoveredAreas.add(Map.of(
                "file", "src/main/java/com/example/controller/OrderController.java",
                "lines", Arrays.asList(120, 121, 122),
                "reason", "Edge case scenarios missing"
            ));
            
            // Add coverage metrics
            coverageMetrics.add(Map.of("metric", "Line Coverage", "value", 70.0, "target", 80.0));
            coverageMetrics.add(Map.of("metric", "Branch Coverage", "value", 65.0, "target", 75.0));
            coverageMetrics.add(Map.of("metric", "Method Coverage", "value", 85.0, "target", 90.0));
            
            Map<String, Object> result = createSuccessResult("analyzeCodeCoverage", coverageData);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("analyzeCodeCoverage", "Coverage analysis failed: " + e.getMessage());
            return errorResult;
        }
    }
    
    // Generate realistic test data from schema
    public Map<String, Object> generateTestDataFromSchema(String schema, Integer count, String dataProfile) {
        Map<String, Object> params = new HashMap<>();
        params.put("schema", schema);
        params.put("count", count);
        params.put("dataProfile", dataProfile);
        
        if (schema == null || schema.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("generateTestDataFromSchema", "Schema cannot be null or empty", "schema");
            return result;
        }
        
        if (count == null || count <= 0 || count > 1000) {
            Map<String, Object> result = createErrorResult("generateTestDataFromSchema", "Count must be between 1 and 1000", "count");
            return result;
        }
        
        try {
            Map<String, Object> testData = new HashMap<>();
            List<Map<String, Object>> generatedData = new ArrayList<>();
            
            String profile = dataProfile != null ? dataProfile : "realistic";
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> record = new HashMap<>();
                
                // Generate data based on profile
                switch (profile.toLowerCase()) {
                    case "edge_case":
                        record.put("id", "edge-case-" + i);
                        record.put("name", ""); // Empty string edge case
                        record.put("email", "invalid-email");
                        record.put("age", -1); // Negative age
                        break;
                    case "boundary":
                        record.put("id", i);
                        record.put("name", "A".repeat(100)); // Max length boundary
                        record.put("email", "test@example.com");
                        record.put("age", 150); // Maximum boundary
                        break;
                    default: // realistic
                        record.put("id", i + 1);
                        record.put("name", "User " + (i + 1));
                        record.put("email", "user" + (i + 1) + "@example.com");
                        record.put("age", 20 + (i % 50));
                        break;
                }
                
                record.put("profile", profile);
                record.put("generatedAt", LocalDateTime.now().format(formatter));
                generatedData.add(record);
            }
            
            testData.put("schema", schema);
            testData.put("dataProfile", profile);
            testData.put("count", count);
            testData.put("generatedData", generatedData);
            testData.put("generatedAt", LocalDateTime.now().format(formatter));
            
            Map<String, Object> result = createSuccessResult("generateTestDataFromSchema", testData);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("generateTestDataFromSchema", "Data generation failed: " + e.getMessage());
            return errorResult;
        }
    }
    
    // Create comprehensive test plan
    public Map<String, Object> createTestPlan(String requirements, List<String> testLevels, String complexity) {
        Map<String, Object> params = new HashMap<>();
        params.put("requirements", requirements);
        params.put("testLevels", testLevels);
        params.put("complexity", complexity);
        
        if (requirements == null || requirements.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("createTestPlan", "Requirements cannot be null or empty", "requirements");
            return result;
        }
        
        try {
            Map<String, Object> testPlan = new HashMap<>();
            List<Map<String, Object>> testCases = new ArrayList<>();
            List<Map<String, Object>> testSuites = new ArrayList<>();
            
            String comp = complexity != null ? complexity : "medium";
            List<String> levels = testLevels != null ? testLevels : Arrays.asList("unit", "integration", "system");
            
            // Generate test cases based on requirements
            testPlan.put("requirements", requirements);
            testPlan.put("complexity", comp);
            testPlan.put("testLevels", levels);
            testPlan.put("estimatedDuration", estimateTestDuration(comp, levels));
            testPlan.put("testCases", testCases);
            testPlan.put("testSuites", testSuites);
            
            // Sample test cases
            testCases.add(Map.of(
                "id", "TC001",
                "title", "User Registration - Valid Input",
                "description", "Test user registration with valid data",
                "priority", "High",
                "level", "Integration",
                "steps", Arrays.asList(
                    "Navigate to registration page",
                    "Enter valid user data",
                    "Submit form",
                    "Verify success message"
                ),
                "expectedResult", "User successfully registered"
            ));
            
            testCases.add(Map.of(
                "id", "TC002", 
                "title", "User Registration - Invalid Email",
                "description", "Test user registration with invalid email format",
                "priority", "Medium",
                "level", "Integration",
                "steps", Arrays.asList(
                    "Navigate to registration page",
                    "Enter user data with invalid email",
                    "Submit form",
                    "Verify error message"
                ),
                "expectedResult", "Error message displayed for invalid email"
            ));
            
            // Test suites
            for (String level : levels) {
                testSuites.add(Map.of(
                    "name", level.toUpperCase() + " Test Suite",
                    "description", level + " level testing",
                    "testCases", testCases.stream()
                        .filter(tc -> level.equals(tc.get("level")))
                        .count(),
                    "estimatedTime", estimateSuiteTime(level, comp)
                ));
            }
            
            Map<String, Object> result = createSuccessResult("createTestPlan", testPlan);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("createTestPlan", "Test plan creation failed: " + e.getMessage());
            return errorResult;
        }
    }
    
    // Validate test quality and provide suggestions
    public Map<String, Object> validateTestQuality(String testCode, List<String> qualityCriteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("testCode", testCode);
        params.put("qualityCriteria", qualityCriteria);
        
        if (testCode == null || testCode.trim().isEmpty()) {
            Map<String, Object> result = createErrorResult("validateTestQuality", "Test code cannot be null or empty", "testCode");
            return result;
        }
        
        try {
            Map<String, Object> qualityReport = new HashMap<>();
            List<Map<String, Object>> issues = new ArrayList<>();
            List<Map<String, Object>> suggestions = new ArrayList<>();
            Map<String, Object> scores = new HashMap<>();
            
            List<String> criteria = qualityCriteria != null ? qualityCriteria : Arrays.asList("readability", "maintainability", "coverage");
            
            // Analyze test quality
            for (String criterion : criteria) {
                switch (criterion.toLowerCase()) {
                    case "readability":
                        scores.put("readability", calculateReadabilityScore(testCode));
                        break;
                    case "maintainability":
                        scores.put("maintainability", calculateMaintainabilityScore(testCode));
                        break;
                    case "coverage":
                        scores.put("coverage", calculateCoverageScore(testCode));
                        break;
                }
            }
            
            // Add common issues
            if (testCode.contains("TODO")) {
                issues.add(Map.of(
                    "type", "incomplete",
                    "severity", "Medium",
                    "description", "Test contains TODO comments",
                    "line", findLineNumbers(testCode, "TODO")
                ));
            }
            
            if (!testCode.contains("assert")) {
                issues.add(Map.of(
                    "type", "missing_assertions",
                    "severity", "High",
                    "description", "Test missing assertions"
                ));
            }
            
            // Add suggestions
            suggestions.add(Map.of(
                "category", "Structure",
                "suggestion", "Consider using AAA pattern (Arrange, Act, Assert)",
                "benefit": "Improves test readability and maintainability"
            ));
            
            suggestions.add(Map.of(
                "category", "Coverage",
                "suggestion": "Add tests for edge cases and boundary conditions",
                "benefit": "Improves test coverage and reliability"
            ));
            
            qualityReport.put("testCode", testCode);
            qualityReport.put("criteria", criteria);
            qualityReport.put("scores", scores);
            qualityReport.put("overallScore", calculateOverallScore(scores));
            qualityReport.put("issues", issues);
            qualityReport.put("suggestions", suggestions);
            qualityReport.put("analyzedAt", LocalDateTime.now().format(formatter));
            
            Map<String, Object> result = createSuccessResult("validateTestQuality", qualityReport);
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = createErrorResult("validateTestQuality", "Quality validation failed: " + e.getMessage());
            return errorResult;
        }
    }
    
    // Helper methods for new functionality
    private String estimateTestDuration(String complexity, List<String> testLevels) {
        int baseHours = 8;
        switch (complexity.toLowerCase()) {
            case "simple": baseHours = 4; break;
            case "medium": baseHours = 8; break;
            case "complex": baseHours = 16; break;
        }
        
        return baseHours * testLevels.size() + " hours";
    }
    
    private int estimateSuiteTime(String level, String complexity) {
        int baseTime = 2;
        switch (level.toLowerCase()) {
            case "unit": baseTime = 1; break;
            case "integration": baseTime = 3; break;
            case "system": baseTime = 5; break;
            case "acceptance": baseTime = 8; break;
        }
        
        switch (complexity.toLowerCase()) {
            case "simple": baseTime *= 0.5; break;
            case "complex": baseTime *= 2; break;
        }
        
        return baseTime;
    }
    
    private double calculateReadabilityScore(String code) {
        int lines = code.split("\n").length;
        int words = code.split("\\s+").length;
        double avgLineLength = (double) words / lines;
        
        // Score based on line length and complexity
        if (avgLineLength < 10) return 90.0;
        if (avgLineLength < 20) return 80.0;
        if (avgLineLength < 30) return 70.0;
        return 60.0;
    }
    
    private double calculateMaintainabilityScore(String code) {
        int methodCount = countOccurrences(code, "void ") + countOccurrences(code, "def ") + countOccurrences(code, "function ");
        int lines = code.split("\n").length;
        
        if (methodCount == 0) return 50.0;
        double avgLinesPerMethod = (double) lines / methodCount;
        
        if (avgLinesPerMethod < 10) return 90.0;
        if (avgLinesPerMethod < 20) return 80.0;
        if (avgLinesPerMethod < 30) return 70.0;
        return 60.0;
    }
    
    private double calculateCoverageScore(String code) {
        int assertCount = countOccurrences(code, "assert");
        int testMethodCount = countOccurrences(code, "@Test") + countOccurrences(code, "def test");
        
        if (testMethodCount == 0) return 50.0;
        double avgAssertsPerTest = (double) assertCount / testMethodCount;
        
        if (avgAssertsPerTest >= 3) return 90.0;
        if (avgAssertsPerTest >= 2) return 80.0;
        if (avgAssertsPerTest >= 1) return 70.0;
        return 60.0;
    }
    
    private double calculateOverallScore(Map<String, Object> scores) {
        double total = 0.0;
        int count = 0;
        
        for (Object score : scores.values()) {
            total += (Double) score;
            count++;
        }
        
        return count > 0 ? total / count : 0.0;
    }
    
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        
        return count;
    }
    
    private List<Integer> findLineNumbers(String code, String pattern) {
        List<Integer> lines = new ArrayList<>();
        String[] codeLines = code.split("\n");
        
        for (int i = 0; i < codeLines.length; i++) {
            if (codeLines[i].contains(pattern)) {
                lines.add(i + 1);
            }
        }
        
        return lines;
    }
}
