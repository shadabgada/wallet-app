package com.gaming.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    private Long betId;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "CASH_RATIO")
    private double cashRatio;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BetStatus betStatus;

    @ManyToOne
    @JoinColumn(name = "playerId", nullable = false)
    private Player player;

}
