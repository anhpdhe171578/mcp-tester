package com.example.mcp.mcp_tester.mcp;

import com.example.mcp.mcp_tester.jsonrpc.JsonRpcRequest;
import com.example.mcp.mcp_tester.jsonrpc.JsonRpcResponse;
import com.example.mcp.mcp_tester.testing.TestingService;
import java.util.*;

public class McpDispatcher {
    
    private final TestingService testingService;
    
    public McpDispatcher(TestingService testingService) {
        this.testingService = testingService;
    }
    
    public JsonRpcResponse dispatch(JsonRpcRequest request) {
        try {
            switch (request.method) {
                case "initialize":
                    return handleInitialize(request);
                case "tools/list":
                    return handleToolsList(request);
                case "tools/call":
                    return handleToolsCall(request);
                case "ping":
                    return handlePing(request);
                default:
                    return JsonRpcResponse.error(request.id, -32601, "Method not found: " + request.method);
            }
        } catch (Exception e) {
            return JsonRpcResponse.error(request.id, -32603, "Internal error: " + e.getMessage());
        }
    }
    
    private JsonRpcResponse handleInitialize(JsonRpcRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", Map.of(
            "tools", Map.of(),
            "logging", Map.of()
        ));
        result.put("serverInfo", Map.of(
            "name", "mcp-tester",
            "version", "1.0.0"
        ));
        return new JsonRpcResponse(request.id, result);
    }
    
    private JsonRpcResponse handleToolsList(JsonRpcRequest request) {
        List<Map<String, Object>> tools = Arrays.asList(
            Map.of(
                "name", "generate_test_data",
                "description", "Generate test data for various types (user, product, string, address)",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "type", Map.of("type", "string", "description", "Type of data to generate (user, product, string, address)"),
                        "count", Map.of("type", "integer", "description", "Number of items to generate (1-1000)")
                    ),
                    "required", Arrays.asList("type", "count")
                )
            ),
            Map.of(
                "name", "validate_data",
                "description", "Validate data against regex pattern",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "data", Map.of("type", "string", "description", "Data to validate"),
                        "pattern", Map.of("type", "string", "description", "Regex pattern to match")
                    ),
                    "required", Arrays.asList("data", "pattern")
                )
            ),
            Map.of(
                "name", "generate_test_patterns",
                "description", "Generate common regex patterns for testing",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "type", Map.of("type", "string", "description", "Pattern type (email, phone, url, date)")
                    ),
                    "required", Arrays.asList("type")
                )
            ),
            Map.of(
                "name", "calculate_stats",
                "description", "Calculate comprehensive statistics for a list of numbers",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "numbers", Map.of("type", "array", "items", Map.of("type", "string"), "description", "List of numbers as strings")
                    ),
                    "required", Arrays.asList("numbers")
                )
            ),
            Map.of(
                "name", "generate_test_scenarios",
                "description", "Generate test scenarios for different categories",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "category", Map.of("type", "string", "description", "Scenario category (api, form, database)")
                    ),
                    "required", Arrays.asList("category")
                )
            ),
            Map.of(
                "name", "store_test_data",
                "description", "Store test data with a key",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "key", Map.of("type", "string", "description", "Storage key"),
                        "value", Map.of("description", "Value to store")
                    ),
                    "required", Arrays.asList("key", "value")
                )
            ),
            Map.of(
                "name", "get_test_data",
                "description", "Retrieve stored test data",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "key", Map.of("type", "string", "description", "Storage key")
                    ),
                    "required", Arrays.asList("key")
                )
            ),
            Map.of(
                "name", "list_test_data",
                "description", "List all stored test data with preview",
                "inputSchema", Map.of("type", "object")
            ),
            Map.of(
                "name", "clear_test_data",
                "description", "Clear all stored test data",
                "inputSchema", Map.of("type", "object")
            ),
            Map.of(
                "name", "get_operation_history",
                "description", "Get operation history and logs",
                "inputSchema", Map.of("type", "object")
            ),
            Map.of(
                "name", "generate_random_strings",
                "description", "Generate random strings with customizable charset",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "count", Map.of("type", "integer", "description", "Number of strings to generate (1-1000)"),
                        "length", Map.of("type", "integer", "description", "Length of each string (1-1000)"),
                        "charset", Map.of("type", "string", "description", "Character set (optional)")
                    ),
                    "required", Arrays.asList("count", "length")
                )
            ),
            Map.of(
                "name", "performance_test",
                "description", "Run performance tests for various operations",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "operation", Map.of("type", "string", "description", "Operation type (string_concat, math_calculation, hash_calculation)"),
                        "iterations", Map.of("type", "integer", "description", "Number of iterations (1-100000)")
                    ),
                    "required", Arrays.asList("operation", "iterations")
                )
            )
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("tools", tools);
        return new JsonRpcResponse(request.id, result);
    }
    
    private JsonRpcResponse handleToolsCall(JsonRpcRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.params;
        String name = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        Map<String, Object> result = new HashMap<>();
        
        switch (name) {
            case "generate_test_data":
                String type = (String) arguments.get("type");
                Integer count = (Integer) arguments.get("count");
                result = testingService.generateTestData(type, count);
                break;
            case "validate_data":
                String data = (String) arguments.get("data");
                String pattern = (String) arguments.get("pattern");
                result = testingService.validateData(data, pattern);
                break;
            case "generate_test_patterns":
                String patternType = (String) arguments.get("type");
                result = testingService.generateTestPatterns(patternType);
                break;
            case "calculate_stats":
                List<String> numbers = (List<String>) arguments.get("numbers");
                result = testingService.calculateStats(numbers);
                break;
            case "generate_test_scenarios":
                String category = (String) arguments.get("category");
                result = testingService.generateTestScenarios(category);
                break;
            case "store_test_data":
                String key = (String) arguments.get("key");
                Object value = arguments.get("value");
                result = testingService.storeTestData(key, value);
                break;
            case "get_test_data":
                String getKey = (String) arguments.get("key");
                result = testingService.getTestData(getKey);
                break;
            case "list_test_data":
                result = testingService.listTestData();
                break;
            case "clear_test_data":
                result = testingService.clearTestData();
                break;
            case "get_operation_history":
                result = testingService.getOperationHistory();
                break;
            case "generate_random_strings":
                Integer strCount = (Integer) arguments.get("count");
                Integer strLength = (Integer) arguments.get("length");
                String charset = (String) arguments.get("charset");
                result = testingService.generateRandomStrings(strCount, strLength, charset);
                break;
            case "performance_test":
                String operation = (String) arguments.get("operation");
                Integer iterations = (Integer) arguments.get("iterations");
                result = testingService.performanceTest(operation, iterations);
                break;
            default:
                return JsonRpcResponse.error(request.id, -32601, "Unknown tool: " + name);
        }
        
        Map<String, Object> toolResult = new HashMap<>();
        toolResult.put("content", Arrays.asList(Map.of(
            "type", "text",
            "text", "Tool '" + name + "' executed successfully"
        )));
        toolResult.put("isError", result.containsKey("error") || (result.containsKey("success") && !(Boolean) result.get("success")));
        toolResult.put("result", result);
        
        return new JsonRpcResponse(request.id, toolResult);
    }
    
    private JsonRpcResponse handlePing(JsonRpcRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "pong");
        result.put("timestamp", System.currentTimeMillis());
        return new JsonRpcResponse(request.id, result);
    }
}
