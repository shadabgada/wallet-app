package com.gaming.wallet.service;

import com.gaming.wallet.entity.Player;
import com.gaming.wallet.entity.Wallet;
import com.gaming.wallet.exception.PlayerNotFoundException;
import com.gaming.wallet.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    public void save(Player player) {
        playerRepository.save(player);
    }

    public void savePlayerWallet(long playerId, Wallet wallet) {
        Optional<Player> player = playerRepository.getPlayerByPlayerId(playerId);
        if (player.isPresent()) {
            player.get().setWallet(wallet);

            // update the player wallet
            playerRepository.save(player.get());
        } else {
            throw new PlayerNotFoundException("Player not found");
        }

    }

    public Wallet getPlayerWalletById(long playerId) {
        return getPlayerById(playerId).getWallet();
    }

    public Player getPlayerById(long playerId) {
        Optional<Player> player = playerRepository.getPlayerByPlayerId(playerId);
        return player.orElseThrow(() -> new PlayerNotFoundException("Player not found"));
    }

}
