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
                "name", "read_file",
                "description", "Read file content for analysis and test generation",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "filePath", Map.of("type", "string", "description", "Path to the file to read")
                    ),
                    "required", Arrays.asList("filePath")
                )
            ),
            Map.of(
                "name", "generate_tests_from_file",
                "description", "Automatically generate test cases based on file content",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "filePath", Map.of("type", "string", "description", "Path to the source file"),
                        "testType", Map.of("type", "string", "description", "Type of tests to generate (unit, integration, api, validation, performance, generic)")
                    ),
                    "required", Arrays.asList("filePath", "testType")
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
            case "read_file":
                String filePath = (String) arguments.get("filePath");
                result = testingService.readFile(filePath);
                break;
            case "generate_tests_from_file":
                String testFilePath = (String) arguments.get("filePath");
                String testType = (String) arguments.get("testType");
                result = testingService.generateTestsFromFile(testFilePath, testType);
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
