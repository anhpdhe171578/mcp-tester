package com.example.mcp.mcp_tester.jsonrpc;

import java.util.Map;

public class JsonRpcRequest {
    public String jsonrpc;
    public String id;
    public String method;
    public Map<String, Object> params;
}
