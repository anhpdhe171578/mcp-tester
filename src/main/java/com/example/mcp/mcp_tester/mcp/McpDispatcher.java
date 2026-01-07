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
                        "filePath", Map.of("type", "string", "description", "Path to file to read")
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
                        "filePath", Map.of("type", "string", "description", "Path to source file"),
                        "testType", Map.of("type", "string", "description", "Type of tests to generate (unit, integration, api, validation, performance, generic)")
                    ),
                    "required", Arrays.asList("filePath", "testType")
                )
            ),
            Map.of(
                "name", "analyze_code_coverage",
                "description", "Analyze code coverage and identify untested areas",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "projectPath", Map.of("type", "string", "description", "Path to project directory"),
                        "coverageType", Map.of("type", "string", "description", "Coverage analysis type (line, branch, method)")
                    ),
                    "required", Arrays.asList("projectPath")
                )
            ),
            Map.of(
                "name", "generate_test_data_from_schema",
                "description", "Generate realistic test data from JSON schema or database schema",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "schema", Map.of("type", "string", "description", "JSON schema or database schema"),
                        "count", Map.of("type", "integer", "description", "Number of test records to generate"),
                        "dataProfile", Map.of("type", "string", "description", "Data profile (realistic, edge_case, boundary)")
                    ),
                    "required", Arrays.asList("schema", "count")
                )
            ),
            Map.of(
                "name", "create_test_plan",
                "description", "Create comprehensive test plan from requirements or user stories",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "requirements", Map.of("type", "string", "description", "Requirements text or user stories"),
                        "testLevels", Map.of("type", "array", "items", Map.of("type", "string"), "description", "Test levels (unit, integration, system, acceptance)"),
                        "complexity", Map.of("type", "string", "description", "Project complexity (simple, medium, complex)")
                    ),
                    "required", Arrays.asList("requirements")
                )
            ),
            Map.of(
                "name", "validate_test_quality",
                "description", "Validate test quality and provide improvement suggestions",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "testCode", Map.of("type", "string", "description", "Test code to validate"),
                        "qualityCriteria", Map.of("type", "array", "items", Map.of("type", "string"), "description", "Quality criteria (readability, maintainability, coverage)")
                    ),
                    "required", Arrays.asList("testCode")
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
            case "analyze_code_coverage":
                String projectPath = (String) arguments.get("projectPath");
                String coverageType = (String) arguments.get("coverageType");
                result = testingService.analyzeCodeCoverage(projectPath, coverageType);
                break;
            case "generate_test_data_from_schema":
                String schema = (String) arguments.get("schema");
                Integer count = (Integer) arguments.get("count");
                String dataProfile = (String) arguments.get("dataProfile");
                result = testingService.generateTestDataFromSchema(schema, count, dataProfile);
                break;
            case "create_test_plan":
                String requirements = (String) arguments.get("requirements");
                @SuppressWarnings("unchecked")
                List<String> testLevels = (List<String>) arguments.get("testLevels");
                String complexity = (String) arguments.get("complexity");
                result = testingService.createTestPlan(requirements, testLevels, complexity);
                break;
            case "validate_test_quality":
                String testCode = (String) arguments.get("testCode");
                @SuppressWarnings("unchecked")
                List<String> qualityCriteria = (List<String>) arguments.get("qualityCriteria");
                result = testingService.validateTestQuality(testCode, qualityCriteria);
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
