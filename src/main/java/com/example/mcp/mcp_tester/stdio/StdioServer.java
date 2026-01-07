package com.example.mcp.mcp_tester.stdio;

import com.example.mcp.mcp_tester.testing.TestingService;
import com.example.mcp.mcp_tester.jsonrpc.JsonRpcRequest;
import com.example.mcp.mcp_tester.jsonrpc.JsonRpcResponse;
import com.example.mcp.mcp_tester.mcp.McpDispatcher;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class StdioServer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final McpDispatcher dispatcher;

    public StdioServer() {
        this.dispatcher = new McpDispatcher(new TestingService());
    }

    public void start() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out, true);

        String line;
        while ((line = in.readLine()) != null) {
            try {
                JsonRpcRequest request = mapper.readValue(line, JsonRpcRequest.class);
                JsonRpcResponse response = dispatcher.dispatch(request);
                out.println(mapper.writeValueAsString(response));
                out.flush();
            } catch (Exception e) {
                JsonRpcResponse errorResponse = JsonRpcResponse.error("unknown", -32700, "Parse error: " + e.getMessage());
                out.println(mapper.writeValueAsString(errorResponse));
                out.flush();
            }
        }
    }
}
