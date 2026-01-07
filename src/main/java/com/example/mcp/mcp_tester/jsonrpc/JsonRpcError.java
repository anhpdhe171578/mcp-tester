package com.example.mcp.mcp_tester.jsonrpc;

public class JsonRpcError {
    public int code;
    public String message;

    public JsonRpcError() {}

    public JsonRpcError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
