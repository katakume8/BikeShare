package com.bikeshare.service;

/**
 * Service interface for payment processing.
 * Demonstrates external service integration testing (Lab 5).
 */
public interface PaymentService {
    
    /**
     * Processes a payment.
     * @param paymentMethodId payment method identifier
     * @param amount amount to charge
     * @param userId user making the payment
     * @return payment confirmation ID
     * @throws PaymentException if payment fails
     */
    String processPayment(String paymentMethodId, double amount, String userId) throws PaymentException;
    
    /**
     * Processes a refund.
     * @param originalPaymentId original payment ID
     * @param amount amount to refund
     * @return refund confirmation ID
     * @throws PaymentException if refund fails
     */
    String processRefund(String originalPaymentId, double amount) throws PaymentException;
    
    /**
     * Validates a payment method.
     * @param paymentMethodId payment method to validate
     * @param userId user ID
     * @return true if valid
     */
    boolean validatePaymentMethod(String paymentMethodId, String userId);
    
    /**
     * Exception thrown when payment processing fails.
     */
    class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
        
        public PaymentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
