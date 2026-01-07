FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the jar file
COPY target/mcp-tester-1.0.0.jar app.jar

# Create non-root user for security
RUN addgroup --system mcp && adduser --system --group mcp

# Change ownership to mcp user
RUN chown -R mcp:mcp /app

# Switch to non-root user
USER mcp

# Expose port (if needed for future HTTP interface)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD java -jar app.jar --health-check || exit 1

# Run the application
CMD ["java", "-jar", "app.jar"]
