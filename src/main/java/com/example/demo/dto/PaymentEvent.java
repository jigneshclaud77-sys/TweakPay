package com.example.demo.dto;

import java.math.BigDecimal;

public record PaymentEvent(
        String transactionId,
        String accountId,
        BigDecimal amount,
        String status) {
}
