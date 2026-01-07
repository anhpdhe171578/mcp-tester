package com.example.mcp.mcp_tester;

import com.example.mcp.mcp_tester.stdio.StdioServer;

public class McpTesterApplication {

    public static void main(String[] args) throws Exception {
        // KHÔNG start Spring Boot
        StdioServer server = new StdioServer();
        server.start(); // BLOCK tại đây, đọc stdin
    }
}
