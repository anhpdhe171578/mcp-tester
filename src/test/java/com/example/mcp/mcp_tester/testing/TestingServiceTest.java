package com.example.mcp.mcp_tester.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestingServiceTest {

    private TestingService testingService;

    @BeforeEach
    void setUp() {
        testingService = new TestingService();
    }

    @Test
    void testGenerateTestData_UserType() {
        Map<String, Object> result = testingService.generateTestData("user", 5);
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals("user", data.get("type"));
        assertEquals(5, data.get("count"));
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        assertEquals(5, items.size());
        
        Map<String, Object> firstUser = items.get(0);
        assertEquals(1, firstUser.get("id"));
        assertEquals("User 1", firstUser.get("name"));
        assertEquals("user1@example.com", firstUser.get("email"));
        assertTrue(firstUser.containsKey("active"));
        assertTrue(firstUser.containsKey("role"));
    }

    @Test
    void testGenerateTestData_InvalidType() {
        Map<String, Object> result = testingService.generateTestData("", 5);
        
        assertFalse((Boolean) result.get("success"));
        assertTrue(result.containsKey("error"));
        assertEquals("generateTestData", result.get("operation"));
    }

    @Test
    void testGenerateTestData_InvalidCount() {
        Map<String, Object> result = testingService.generateTestData("user", 0);
        
        assertFalse((Boolean) result.get("success"));
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testValidateData_ValidEmail() {
        Map<String, Object> result = testingService.validateData("test@example.com", "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertTrue((Boolean) data.get("valid"));
        assertEquals("test@example.com", data.get("data"));
        assertEquals("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", data.get("pattern"));
    }

    @Test
    void testValidateData_InvalidPattern() {
        Map<String, Object> result = testingService.validateData("test", "[invalid");
        
        assertFalse((Boolean) result.get("success"));
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("Invalid regex pattern"));
    }

    @Test
    void testCalculateStats_ValidNumbers() {
        List<String> numbers = List.of("1", "2", "3", "4", "5");
        Map<String, Object> result = testingService.calculateStats(numbers);
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(5, data.get("count"));
        assertEquals(15.0, data.get("sum"));
        assertEquals(3.0, data.get("average"));
        assertEquals(1.0, data.get("min"));
        assertEquals(5.0, data.get("max"));
        assertEquals(3.0, data.get("median"));
        assertTrue(data.containsKey("standardDeviation"));
    }

    @Test
    void testCalculateStats_MixedValidInvalid() {
        List<String> numbers = List.of("1", "invalid", "3", "4.5", "not_a_number");
        Map<String, Object> result = testingService.calculateStats(numbers);
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(3, data.get("count")); // Only valid numbers
        assertEquals(60.0, data.get("validPercentage")); // 3 out of 5 = 60%
        
        @SuppressWarnings("unchecked")
        List<String> invalidNumbers = (List<String>) data.get("invalidNumbers");
        assertEquals(2, invalidNumbers.size());
        assertTrue(invalidNumbers.contains("invalid"));
        assertTrue(invalidNumbers.contains("not_a_number"));
    }

    @Test
    void testStoreAndGetTestData() {
        Map<String, Object> storeResult = testingService.storeTestData("test_key", "test_value");
        
        assertTrue((Boolean) storeResult.get("success"));
        Map<String, Object> storeData = (Map<String, Object>) storeResult.get("data");
        String storageKey = (String) storeData.get("key");
        
        Map<String, Object> getResult = testingService.getTestData(storageKey);
        assertTrue((Boolean) getResult.get("success"));
        Map<String, Object> getData = (Map<String, Object>) getResult.get("data");
        assertTrue((Boolean) getData.get("found"));
        assertEquals("test_value", getData.get("value"));
    }

    @Test
    void testGetNonExistentData() {
        Map<String, Object> result = testingService.getTestData("non_existent_key");
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertFalse((Boolean) data.get("found"));
        assertTrue(data.containsKey("availableKeys"));
    }

    @Test
    void testListTestData() {
        testingService.storeTestData("key1", "value1");
        testingService.storeTestData("key2", "value2");
        
        Map<String, Object> result = testingService.listTestData();
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(2, data.get("totalKeys"));
        assertEquals(2, data.get("storageSize"));
        assertEquals(0.2, data.get("storagePercentage")); // 2 out of 1000
        assertTrue(data.containsKey("preview"));
    }

    @Test
    void testClearTestData() {
        testingService.storeTestData("key1", "value1");
        testingService.storeTestData("key2", "value2");
        
        Map<String, Object> result = testingService.clearTestData();
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(2, data.get("clearedKeys"));
        assertEquals(0, data.get("storageSize"));
        
        // Verify storage is empty
        Map<String, Object> listResult = testingService.listTestData();
        Map<String, Object> listData = (Map<String, Object>) listResult.get("data");
        assertEquals(0, listData.get("totalKeys"));
    }

    @Test
    void testGenerateRandomStrings() {
        Map<String, Object> result = testingService.generateRandomStrings(5, 10, "abc");
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(5, data.get("count"));
        assertEquals(10, data.get("length"));
        assertEquals("abc", data.get("charset"));
        
        @SuppressWarnings("unchecked")
        List<String> strings = (List<String>) data.get("strings");
        assertEquals(5, strings.size());
        
        for (String str : strings) {
            assertEquals(10, str.length());
            assertTrue(str.matches("[abc]+")); // Only contains a, b, or c
        }
    }

    @Test
    void testGenerateTestPatterns_Email() {
        Map<String, Object> result = testingService.generateTestPatterns("email");
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> patterns = (Map<String, Object>) data;
        assertTrue(patterns.containsKey("basic"));
        assertTrue(patterns.containsKey("strict"));
        assertTrue(patterns.containsKey("gmail"));
    }

    @Test
    void testGenerateTestScenarios_API() {
        Map<String, Object> result = testingService.generateTestScenarios("api");
        
        assertTrue((Boolean) result.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> scenarios = (List<Map<String, Object>>) result.get("data");
        assertTrue(scenarios.size() >= 3); // Should have success, not found, and error scenarios
        
        boolean hasSuccess = scenarios.stream().anyMatch(s -> "Success Response".equals(s.get("name")));
        boolean hasNotFound = scenarios.stream().anyMatch(s -> "Not Found".equals(s.get("name")));
        boolean hasError = scenarios.stream().anyMatch(s -> "Server Error".equals(s.get("name")));
        
        assertTrue(hasSuccess);
        assertTrue(hasNotFound);
        assertTrue(hasError);
    }

    @Test
    void testPerformanceTest() {
        Map<String, Object> result = testingService.performanceTest("string_concat", 1000);
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals("string_concat", data.get("operation"));
        assertEquals(1000, data.get("iterations"));
        assertTrue(data.containsKey("durationMs"));
        assertTrue(data.containsKey("operationsPerSecond"));
        assertTrue(data.containsKey("averageTimePerOperation"));
    }

    @Test
    void testGetOperationHistory() {
        // Perform some operations to generate history
        testingService.generateTestData("user", 2);
        testingService.validateData("test@test.com", "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        
        Map<String, Object> result = testingService.getOperationHistory();
        
        assertTrue((Boolean) result.get("success"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertTrue(data.containsKey("totalOperations"));
        assertTrue(data.containsKey("history"));
        assertEquals(100, data.get("maxHistorySize"));
    }
}
