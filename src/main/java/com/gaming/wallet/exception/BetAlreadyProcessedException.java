package com.gaming.wallet.exception;

public class BetAlreadyProcessedException extends RuntimeException {
    public BetAlreadyProcessedException(String message) {
        super(message);
    }
}
