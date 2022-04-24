package com.gaming.wallet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponse {
    double cashBalance;
    double bonusBalance;
}