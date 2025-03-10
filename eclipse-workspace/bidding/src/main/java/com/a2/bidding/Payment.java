package com.a2.bidding;

import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Long itemId;
    private Long userId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    // Constructors, Getters, and Setters
    public Payment() {}

    public Payment(Long id, Long itemId, Long userId, double amount, String paymentMethod, String paymentStatus, LocalDateTime paymentTime) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
}