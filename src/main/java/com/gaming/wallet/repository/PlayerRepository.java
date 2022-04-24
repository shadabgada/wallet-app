package com.gaming.wallet.repository;

import com.gaming.wallet.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> getPlayerByPlayerId(long playerId);

    Optional<Player> findPlayerByUserName(String username);

}