package com.gaming.wallet.controller;

import com.gaming.wallet.dto.TransactionResponse;
import com.gaming.wallet.dto.WalletRequest;
import com.gaming.wallet.dto.WalletResponse;
import com.gaming.wallet.entity.Transaction;
import com.gaming.wallet.entity.Wallet;
import com.gaming.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wallet")
public class TransactionController {

    // default page number
    public final int PAGE_NO = 0;

    // default page size
    public final int PAGE_SIZE = 10;

    @Autowired
    TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> depositAmount(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(convertToDto(transactionService.depositAmount(walletRequest)), HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdrawAmount(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(convertToDto(transactionService.withdrawAmount(walletRequest)), HttpStatus.OK);
    }

    @PostMapping("/bet")
    public ResponseEntity<WalletResponse> registerBet(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(convertToDto(transactionService.registerBet(walletRequest)), HttpStatus.OK);
    }

    @PostMapping("/win")
    public ResponseEntity<WalletResponse> registerWin(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(convertToDto(transactionService.registerWin(walletRequest)), HttpStatus.OK);
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<WalletResponse> getBalanceByPlayerId(@PathVariable long id) {
        return new ResponseEntity<>(convertToDto(transactionService.getWalletByPlayerId(id)), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(required = false) Optional<Long> transactionId,
            @RequestParam(required = false) Optional<Long> playerId,
            @RequestParam(required = false) Optional<String> type,
            @RequestParam(required = false) Optional<Double> amount,
            @RequestParam(defaultValue = "TRANSACTION_ID") String sortBy) {
        return new ResponseEntity<>(
                convertToDto(
                        transactionService.getAllTransactions(
                                page.orElse(PAGE_NO),
                                size.orElse(PAGE_SIZE),
                                transactionId.orElse(0L),
                                playerId.orElse(0L),
                                type.orElse(null),
                                amount.orElse(0D),
                                sortBy)), HttpStatus.OK);
    }

    WalletResponse convertToDto(Wallet wallet) {
        return WalletResponse.builder()
                .bonusBalance(wallet.getBonusAmount())
                .cashBalance(wallet.getCashAmount())
                .build();
    }

    List<TransactionResponse> convertToDto(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    TransactionResponse convertToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .playerId(transaction.getPlayer().getPlayerId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .build();
    }

}
