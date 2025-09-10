package com.bikeshare.web;

/**
 * Legacy wrapper kept for compatibility. Delegates to SimpleBikeShareWebServer.
 */
public class BikeShareWebServer {
    public static void main(String[] args) throws Exception {
        System.out.println("BikeShareWebServer is deprecated. Starting SimpleBikeShareWebServer instead...");
        SimpleBikeShareWebServer.main(args);
    }
}
