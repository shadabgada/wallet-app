package com.gaming.wallet.service;

import com.gaming.wallet.dto.WalletRequest;
import com.gaming.wallet.entity.Bet;
import com.gaming.wallet.entity.BetStatus;
import com.gaming.wallet.entity.Player;
import com.gaming.wallet.entity.Transaction;
import com.gaming.wallet.entity.TransactionType;
import com.gaming.wallet.entity.Wallet;
import com.gaming.wallet.exception.BetAlreadyProcessedException;
import com.gaming.wallet.exception.BetNotFoundException;
import com.gaming.wallet.exception.InsufficientBalanceException;
import com.gaming.wallet.exception.InvalidAmountException;
import com.gaming.wallet.exception.TransactionConflictException;
import com.gaming.wallet.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.gaming.wallet.entity.TransactionType.BET;
import static com.gaming.wallet.entity.TransactionType.DEPOSIT;
import static com.gaming.wallet.entity.TransactionType.WIN;
import static com.gaming.wallet.entity.TransactionType.WITHDRAW;

@Service
public class TransactionService {

    @Autowired
    PlayerService playerService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BetService betService;

    public Wallet depositAmount(WalletRequest walletRequest) {

        Wallet wallet = validateTransaction(walletRequest.getTransactionId(), walletRequest.getAmount(), DEPOSIT);
        if (wallet != null)
            return wallet;

        Player player = playerService.getPlayerById(walletRequest.getPlayerId());
        wallet = player.getWallet();

        double amount = walletRequest.getAmount();
        if (amount <= 0)
            throw new InvalidAmountException("Deposit amount of €0 or less not allowed");

        wallet.setCashAmount(wallet.getCashAmount() + amount);
        if (amount >= 100)
            wallet.setBonusAmount(wallet.getBonusAmount() + amount);

        // update the player wallet
        player.setWallet(wallet);
        playerService.save(player);

        // add transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(walletRequest.getTransactionId())
                .type(DEPOSIT)
                .amount(amount)
                .cash(wallet.getCashAmount())
                .bonus(wallet.getBonusAmount())
                .player(player)
                .build();

        transactionRepository.save(transaction);

        return wallet;
    }

    public Wallet withdrawAmount(WalletRequest walletRequest) {

        Wallet wallet = validateTransaction(walletRequest.getTransactionId(), walletRequest.getAmount(), WITHDRAW);
        if (wallet != null)
            return wallet;

        double amount = walletRequest.getAmount();
        if (amount <= 0)
            throw new InvalidAmountException("Withdrawal amount of €0 or less not allowed");

        Player player = playerService.getPlayerById(walletRequest.getPlayerId());
        wallet = player.getWallet();
        if (amount > wallet.getCashAmount())
            throw new InsufficientBalanceException("Insufficient balance");

        wallet.setCashAmount(wallet.getCashAmount() - amount);

        // update the player wallet
        player.setWallet(wallet);
        playerService.save(player);

        // add transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(walletRequest.getTransactionId())
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .cash(wallet.getCashAmount())
                .bonus(wallet.getBonusAmount())
                .player(player)
                .build();
        transactionRepository.save(transaction);

        return wallet;
    }

    public Wallet registerBet(WalletRequest walletRequest) {

        Wallet wallet = validateTransaction(walletRequest.getTransactionId(), walletRequest.getAmount(), BET);
        if (wallet != null)
            return wallet;

        double betAmount = walletRequest.getAmount();
        if (betAmount <= 0)
            throw new InvalidAmountException("Bets with a value of €0 or less not allowed");

        Player player = playerService.getPlayerById(walletRequest.getPlayerId());
        wallet = player.getWallet();

        if (wallet.getCashAmount() + wallet.getBonusAmount() < betAmount)
            throw new InsufficientBalanceException("Insufficient balance");

        double cashRatio;
        double cashAmount = wallet.getCashAmount();
        if (betAmount <= cashAmount) {
            wallet.setCashAmount(cashAmount - betAmount);
            cashRatio = 1;
        } else {
            wallet.setBonusAmount(wallet.getBonusAmount() - (betAmount - wallet.getCashAmount()));
            wallet.setCashAmount(0L);
            cashRatio = cashAmount / betAmount;
        }

        Bet bet = Bet.builder()
                .betId(walletRequest.getBetId())
                .amount(betAmount)
                .cashRatio(cashRatio)
                .betStatus(BetStatus.PLACED)
                .player(player)
                .build();
        // register the bet
        betService.saveBet(bet);

        // update the player wallet
        player.setWallet(wallet);
        playerService.save(player);

        // add transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(walletRequest.getTransactionId())
                .type(TransactionType.BET)
                .amount(betAmount)
                .cash(wallet.getCashAmount())
                .bonus(wallet.getBonusAmount())
                .player(player)
                .build();
        transactionRepository.save(transaction);
        return wallet;
    }

    public Wallet registerWin(WalletRequest walletRequest) {

        Wallet wallet = validateTransaction(walletRequest.getTransactionId(), walletRequest.getAmount(), WIN);
        if (wallet != null)
            return wallet;

        double winAmount = walletRequest.getAmount();
        if (winAmount <= 0)
            throw new InvalidAmountException("Win amount of less than €0 not allowed");

        Player player = playerService.getPlayerById(walletRequest.getPlayerId());
        wallet = player.getWallet();

        Optional<Bet> bet = betService.findById(walletRequest.getBetId());
        Bet existingBet = bet.orElseThrow(() -> new BetNotFoundException("No bet registered with provided bet id"));

        if (existingBet.getBetStatus().name().equals(BetStatus.COMPLETE.name()))
            throw new BetAlreadyProcessedException("Win is already issued for the provided bet id");

        double cashRatio = existingBet.getCashRatio();
        wallet.setCashAmount(wallet.getCashAmount() + cashRatio * winAmount);
        wallet.setBonusAmount(wallet.getBonusAmount() + (1 - cashRatio) * winAmount);

        // update Bet status
        existingBet.setBetStatus(BetStatus.COMPLETE);
        betService.saveBet(existingBet);

        // update the player wallet
        player.setWallet(wallet);
        playerService.save(player);

        // add transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(walletRequest.getTransactionId())
                .type(TransactionType.WIN)
                .amount(winAmount)
                .cash(wallet.getCashAmount())
                .bonus(wallet.getBonusAmount())
                .player(player)
                .build();
        transactionRepository.save(transaction);
        return wallet;
    }

    public Wallet getWalletByPlayerId(long id) {
        return playerService.getPlayerWalletById(id);
    }

    public List<Transaction> getAllTransactions(
            int page,
            int size,
            long transactionId,
            long playerId,
            String type,
            double amount,
            String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Transaction> transactionPage = transactionRepository.findAllTransactionByCriteria(
                transactionId,
                playerId,
                type,
                amount,
                pageable);
        return transactionPage.getContent();
    }

    private Wallet validateTransaction(long transactionId, double amount, TransactionType transactionType) {
        Optional<Transaction> transaction = transactionRepository.findByTransactionId(transactionId);
        if (transaction.isPresent()) {
            Transaction existing = transaction.get();

            // check if transaction id is repeated for different operation
            if (!existing.getType().name().equals(transactionType.name()))
                throw new TransactionConflictException("Transaction with provided id already exists for different operation");
                // check if transaction id is repeated for same operation but different amount
            else if (existing.getAmount() != amount)
                throw new TransactionConflictException("Transaction with provided id already exists with different amount");

            return Wallet.builder()
                    .cashAmount(existing.getCash())
                    .bonusAmount(existing.getBonus())
                    .build();
        }
        return null;
    }

}
