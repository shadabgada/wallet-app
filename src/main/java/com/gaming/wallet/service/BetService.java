package com.gaming.wallet.service;

import com.gaming.wallet.entity.Bet;
import com.gaming.wallet.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BetService {

    @Autowired
    BetRepository betRepository;

    public void saveBet(Bet bet) {
        betRepository.save(bet);
    }

    public Optional<Bet> findById(Long id) {
        return betRepository.findById(id);
    }

    public List<Bet> getAll(){
        return betRepository.findAll();
    }
}
