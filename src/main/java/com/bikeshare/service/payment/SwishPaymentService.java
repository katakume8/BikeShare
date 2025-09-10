package com.bikeshare.service.payment;

/**
 * Swish payment integration abstraction (Swedish mobile payment system).
 * Designed to be mocked in tests.
 */
public interface SwishPaymentService {

    /**
     * Initiates a Swish payment request.
     * @param phoneNumber E.164 formatted mobile number (e.g., +46701234567)
     * @param amount SEK amount (>= 1.0)
     * @param message Payment message shown in the Swish app
     * @return paymentRequestId reference ID
     */
    String requestPayment(String phoneNumber, double amount, String message);

    /**
     * Checks if a Swish payment has been completed/approved.
     * @param paymentRequestId reference from requestPayment
     * @return true if paid
     */
    boolean isPaymentCompleted(String paymentRequestId);
}
