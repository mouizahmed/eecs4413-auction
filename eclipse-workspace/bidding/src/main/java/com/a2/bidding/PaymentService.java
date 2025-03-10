package com.a2.bidding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentService {
    private final List<Payment> payments = new ArrayList<>();
    private final AtomicLong paymentIdCounter = new AtomicLong(1);

    // Process a payment
    public Payment processPayment(Payment payment) {
        payment.setId(paymentIdCounter.getAndIncrement());
        payment.setPaymentStatus("Completed"); // Set default status
        payments.add(payment);
        return payment;
    }
}