package com.gaming.wallet.controller;

import com.gaming.wallet.exception.BetNotFoundException;
import com.gaming.wallet.exception.InsufficientBalanceException;
import com.gaming.wallet.exception.InvalidAmountException;
import com.gaming.wallet.exception.PlayerNotFoundException;
import com.gaming.wallet.exception.TransactionConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = PlayerNotFoundException.class)
    public String handlePlayerNotFoundException(PlayerNotFoundException playerNotFoundException) {
        log.warn("PlayerNotFoundException Found ", playerNotFoundException);
        return playerNotFoundException.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = BetNotFoundException.class)
    public String handleBetNotFoundException(BetNotFoundException betNotFoundException) {
        log.warn("BetNotFoundException Found ", betNotFoundException);
        return betNotFoundException.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidAmountException.class)
    public String handleInvalidAmountException(InvalidAmountException invalidAmountException) {
        log.warn("InvalidAmountException Found ", invalidAmountException);
        return invalidAmountException.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        log.warn("IllegalArgumentException Found ", illegalArgumentException);
        return illegalArgumentException.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException insufficientBalanceException) {
        log.warn("InsufficientBalanceException Found ", insufficientBalanceException);
        return insufficientBalanceException.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public String handleInsufficientBalanceException(SQLSyntaxErrorException sqlSyntaxErrorException) {
        log.warn("SQLSyntaxErrorException Found ", sqlSyntaxErrorException);
        return sqlSyntaxErrorException.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = TransactionConflictException.class)
    public String handleTransactionConflictException(TransactionConflictException transactionConflictException) {
        log.warn("TransactionConflictException Found ", transactionConflictException);
        return transactionConflictException.getMessage();
    }
}
