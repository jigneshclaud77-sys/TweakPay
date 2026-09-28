package com.example.demo.dto;

import java.math.BigDecimal;

public record PaymentEvent(
        String paymentId,
        String accountId,
        BigDecimal amount,
        String currency) {
}
