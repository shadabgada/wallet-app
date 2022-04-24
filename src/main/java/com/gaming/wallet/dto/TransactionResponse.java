package com.gaming.wallet.dto;

import com.gaming.wallet.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private Long transactionId;
    private Long playerId;
    private TransactionType type;
    private double amount;
}