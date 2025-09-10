package com.bikeshare.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Simple embedded web server for the BikeShare system.
 * Provides a basic web interface for demonstration and testing purposes.
 */
public class SimpleBikeShareWebServer {
    
    private static final int DEFAULT_PORT = 8080;
    private final Server server;
    private final ObjectMapper objectMapper;
    
    public SimpleBikeShareWebServer() {
        this(DEFAULT_PORT);
    }
    
    public SimpleBikeShareWebServer(int port) {
        this.server = new Server(port);
        this.objectMapper = createObjectMapper();
        setupServer();
    }
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
    
    private void setupServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // Static content servlet
        context.addServlet(new ServletHolder(new StaticContentServlet()), "/");
        
    // API endpoints
    context.addServlet(new ServletHolder(new ApiServlet()), "/api/*");
    context.addServlet(new ServletHolder(new ValidationServlet()), "/api/validate/*");
    context.addServlet(new ServletHolder(new PaymentsServlet()), "/api/payments/*");
        
        server.setHandler(context);
    }
    
    public void start() throws Exception {
        server.start();
        // Determine actual bound URI/port after start
        String baseUrl;
        try {
            URI uri = server.getURI();
            baseUrl = uri != null ? uri.toString() : null;
        } catch (Throwable t) {
            baseUrl = null;
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            int actualPort = -1;
            if (server.getConnectors().length > 0 && server.getConnectors()[0] instanceof ServerConnector sc) {
                actualPort = sc.getLocalPort();
            }
            baseUrl = "http://localhost:" + (actualPort > 0 ? actualPort : DEFAULT_PORT) + "/";
        }

        System.out.println("BikeShare Web Server started on " + baseUrl);
        System.out.println("Open your browser to view the testing platform dashboard");
        System.out.println("Endpoints: GET /api/*, POST /api/validate/age, POST /api/payments/swish/*");
    }
    
    public void stop() throws Exception {
        server.stop();
    }
    
    public void waitForShutdown() throws InterruptedException {
        server.join();
    }
    
    /**
     * Servlet for serving static HTML, CSS, and JavaScript files
     */
    private static class StaticContentServlet extends HttpServlet {
        
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                path = "/index.html";
            }
            
            // Serve static files from resources
            InputStream inputStream = getClass().getResourceAsStream("/webapp" + path);
            if (inputStream == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("text/html");
                resp.getWriter().write("<!DOCTYPE html><html><head><title>404 - Not Found</title></head>"
                    + "<body><h1>404 - File Not Found</h1><p>The requested file was not found: " + path + "</p></body></html>");
                return;
            }
            
            // Set content type based on file extension
            if (path.endsWith(".html")) {
                resp.setContentType("text/html; charset=UTF-8");
            } else if (path.endsWith(".css")) {
                resp.setContentType("text/css; charset=UTF-8");
            } else if (path.endsWith(".js")) {
                resp.setContentType("application/javascript; charset=UTF-8");
            }
            
            // Copy content to response
            byte[] buffer = inputStream.readAllBytes();
            resp.getOutputStream().write(buffer);
            inputStream.close();
        }
    }
    
    /**
     * REST API servlet for BikeShare operations
     */
    private class ApiServlet extends HttpServlet {
        
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            
            resp.setContentType("application/json; charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            
            String pathInfo = req.getPathInfo();
            try {
                switch (pathInfo) {
                    case "/bikes":
                        handleGetBikes(resp);
                        break;
                    case "/stations":
                        handleGetStations(resp);
                        break;
                    case "/users":
                        handleGetUsers(resp);
                        break;
                    case "/rides":
                        handleGetRides(resp);
                        break;
                    case "/stats":
                        handleGetStats(resp);
                        break;
                    default:
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\": \"Endpoint not found: " + pathInfo + "\"}");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        }
        
        private void handleGetBikes(HttpServletResponse resp) throws IOException {
            // Sample bike data for demonstration
            Map<String, Object> bikeData = new HashMap<>();
            bikeData.put("message", "BikeShare API - Bikes Endpoint");
            bikeData.put("totalBikes", 15);
            bikeData.put("availableBikes", 12);
            bikeData.put("status", "operational");
            bikeData.put("note", "This is sample data for testing. Implement actual BikeService integration.");
            objectMapper.writeValue(resp.getWriter(), bikeData);
        }
        
        private void handleGetStations(HttpServletResponse resp) throws IOException {
            // Sample station data for demonstration
            Map<String, Object> stationData = new HashMap<>();
            stationData.put("message", "BikeShare API - Stations Endpoint");
            stationData.put("totalStations", 8);
            stationData.put("activeStations", 7);
            stationData.put("status", "operational");
            stationData.put("note", "This is sample data for testing. Implement actual StationService integration.");
            objectMapper.writeValue(resp.getWriter(), stationData);
        }
        
        private void handleGetUsers(HttpServletResponse resp) throws IOException {
            // Sample user data for demonstration
            Map<String, Object> userData = new HashMap<>();
            userData.put("message", "BikeShare API - Users Endpoint");
            userData.put("note", "User data requires authentication in a real system");
            userData.put("registeredUsers", 42);
            userData.put("activeUsers", 38);
            objectMapper.writeValue(resp.getWriter(), userData);
        }
        
        private void handleGetRides(HttpServletResponse resp) throws IOException {
            // Sample ride data for demonstration
            Map<String, Object> rideData = new HashMap<>();
            rideData.put("message", "BikeShare API - Rides Endpoint");
            rideData.put("totalRides", 156);
            rideData.put("activeRides", 3);
            rideData.put("completedRides", 153);
            rideData.put("note", "This is sample data for testing. Implement actual RideService integration.");
            objectMapper.writeValue(resp.getWriter(), rideData);
        }
        
        private void handleGetStats(HttpServletResponse resp) throws IOException {
            // Sample statistics for demonstration
            Map<String, Object> stats = new HashMap<>();
            
            Map<String, Object> bikeStats = new HashMap<>();
            bikeStats.put("totalBikes", 15);
            bikeStats.put("availableBikes", 12);
            bikeStats.put("maintenanceBikes", 2);
            bikeStats.put("rentedBikes", 1);
            
            Map<String, Object> stationStats = new HashMap<>();
            stationStats.put("totalStations", 8);
            stationStats.put("activeStations", 7);
            stationStats.put("fullStations", 2);
            stationStats.put("emptyStations", 1);
            
            stats.put("bikeStats", bikeStats);
            stats.put("stationStats", stationStats);
            stats.put("lastUpdated", System.currentTimeMillis());
            
            objectMapper.writeValue(resp.getWriter(), stats);
        }
    }

    /**
     * Simple validation API to exercise backend logic from the UI
     */
    private class ValidationServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json; charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");

            String path = req.getPathInfo();
            if (path == null) path = "";
            try {
                if ("/age".equals(path)) {
                    // Expect JSON: { "idNumber": "YYYYMMDDNNNN", "auth": true/false }
            Map<String, Object> body = objectMapper.readValue(
                req.getInputStream(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );
                    String idNumber = (String) body.get("idNumber");
                    boolean auth = body.get("auth") != null && Boolean.TRUE.equals(body.get("auth"));

            // Use factory to allow swapping in buggy implementations for exercises
            boolean adult = com.bikeshare.service.validation.AgeCheckFactory
                .create()
                .isAdult(idNumber, auth);
                    Map<String, Object> result = new HashMap<>();
                    result.put("adult", adult);
                    result.put("checkedAt", System.currentTimeMillis());
                    objectMapper.writeValue(resp.getWriter(), result);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), Map.of("error", "Unknown validation endpoint"));
                }
            } catch (IllegalArgumentException iae) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", iae.getMessage()));
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
            }
        }

        @Override
        protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        }
    }

    /**
     * Simple payments API stubs for Swish flows from UI
     */
    private class PaymentsServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json; charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");

            String path = req.getPathInfo();
            if (path == null) path = "";
            try {
                if ("/swish/request".equals(path)) {
            Map<String, Object> body = objectMapper.readValue(
                req.getInputStream(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );
                    String phone = (String) body.get("phone");
                    double amount = body.get("amount") instanceof Number ? ((Number) body.get("amount")).doubleValue() : 0.0;
                    String message = (String) body.getOrDefault("message", "BikeShare ride");
                    // Return fake request id for demo
                    objectMapper.writeValue(resp.getWriter(), Map.of(
                            "requestId", "swish-" + System.currentTimeMillis(),
                            "phone", phone,
                            "amount", amount,
                            "message", message
                    ));
                } else if ("/swish/status".equals(path)) {
            Map<String, Object> body = objectMapper.readValue(
                req.getInputStream(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );
                    String requestId = (String) body.get("requestId");
                    // Random-ish demo status
                    boolean paid = requestId != null && requestId.hashCode() % 2 == 0;
                    objectMapper.writeValue(resp.getWriter(), Map.of("requestId", requestId, "paid", paid));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), Map.of("error", "Unknown payments endpoint"));
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
            }
        }

        @Override
        protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        }
    }
    
    /**
     * Main method to start the web server
     */
    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[0]);
                System.exit(1);
            }
        }
        
        SimpleBikeShareWebServer webServer = new SimpleBikeShareWebServer(port);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down web server...");
                webServer.stop();
            } catch (Exception e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }));
        
        webServer.start();
        webServer.waitForShutdown();
    }
}
