package com.gaming.wallet.dto;

import lombok.Data;

@Data
public class WalletRequest {
    long transactionId;
    long playerId;
    double amount;
    long betId;
}
