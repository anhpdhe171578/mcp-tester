package com.example.mcp.mcp_tester.jsonrpc;

public class JsonRpcResponse {
    public String jsonrpc = "2.0";
    public String id;
    public Object result;
    public JsonRpcError error;

    public JsonRpcResponse() {}

    public JsonRpcResponse(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    public static JsonRpcResponse error(String id, int code, String message) {
        JsonRpcResponse response = new JsonRpcResponse();
        response.id = id;
        response.error = new JsonRpcError(code, message);
        return response;
    }
}
