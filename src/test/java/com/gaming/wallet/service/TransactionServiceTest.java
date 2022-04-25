package com.gaming.wallet.service;


import com.gaming.wallet.dto.WalletRequest;
import com.gaming.wallet.entity.Bet;
import com.gaming.wallet.entity.BetStatus;
import com.gaming.wallet.entity.Player;
import com.gaming.wallet.entity.Wallet;
import com.gaming.wallet.exception.InsufficientBalanceException;
import com.gaming.wallet.exception.InvalidAmountException;
import com.gaming.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionServiceTest {

    @Mock
    PlayerService playerService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    BetService betService;

    @InjectMocks
    TransactionService transactionService;

    @Test
    public void testDepositAmount() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(100);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);

        when(playerService.getPlayerById(1L)).thenReturn(player);

        Wallet walletResponse =
                transactionService.depositAmount(request);

        // check cash and bonus balance
        Assertions.assertEquals(200, walletResponse.getCashAmount());
        Assertions.assertEquals(150, walletResponse.getBonusAmount());
    }

    @Test
    public void testInvalidDepositAmount() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(0L);

        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);
        when(playerService.getPlayerById(1L)).thenReturn(player);

        // check invalid amount exception is thrown
        assertThrows(InvalidAmountException.class, ()-> transactionService.depositAmount(request));
    }

    @Test
    public void testWithdrawAmount() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(100);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);

        when(playerService.getPlayerById(1L)).thenReturn(player);

        Wallet walletResponse =
                transactionService.withdrawAmount(request);

        // check cash and bonus balance
        Assertions.assertEquals(0, walletResponse.getCashAmount());
        Assertions.assertEquals(50, walletResponse.getBonusAmount());
    }

    @Test
    public void testInvalidWithdrawAmount() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(150);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);

        when(playerService.getPlayerById(1L)).thenReturn(player);

        // check insufficient amount exception is thrown
        assertThrows(InsufficientBalanceException.class, ()-> transactionService.withdrawAmount(request));
    }

    @Test
    public void testRegisterBet() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(150);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);

        when(playerService.getPlayerById(1L)).thenReturn(player);

        Wallet walletResponse =
                transactionService.registerBet(request);

        // check cash and bonus balance
        Assertions.assertEquals(0, walletResponse.getCashAmount());
        Assertions.assertEquals(0, walletResponse.getBonusAmount());
    }

    @Test
    public void testInSufficientBalanceOnRegisterBet() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(250);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);

        when(playerService.getPlayerById(1L)).thenReturn(player);

        // check insufficient balance exception is thrown
        assertThrows(InsufficientBalanceException.class, ()-> transactionService.registerBet(request));
    }

    @Test
    public void testRegisterWin() {

        WalletRequest request = new WalletRequest();
        request.setPlayerId(1L);
        request.setAmount(600);
        Player player = new Player();
        Wallet wallet = new Wallet(1L, 100L, 50L, player);
        player.setWallet(wallet);
        when(playerService.getPlayerById(1L)).thenReturn(player);


        Optional<Bet> existingBet = Optional.of(new Bet(1L, 150d, 0.75d, BetStatus.PLACED, player));
        when(betService.findById(any())).thenReturn(existingBet);

        Wallet walletResponse =
                transactionService.registerWin(request);

        // check cash and bonus balance is distributed as expected
        Assertions.assertEquals(550, walletResponse.getCashAmount());
        Assertions.assertEquals(200, walletResponse.getBonusAmount());
    }

}
