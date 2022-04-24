package com.gaming.wallet.repository;

import com.gaming.wallet.entity.Transaction;
import com.gaming.wallet.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(long transactionId);

    Optional<Transaction> findFirstByTypeOrderByIdDesc(TransactionType type);

    Page<Transaction> findAll(Pageable pageable);

    @Query(value = "SELECT * from Transaction t " +
            "WHERE ( (:transactionId = 0 OR t.TRANSACTION_ID = :transactionId )" +
            "AND ( :playerId = 0 OR t.PLAYER_ID = :playerId ) " +
            "AND ( :type is null OR t.TYPE = :type ) " +
            "AND ( :amount = 0 OR t.AMOUNT = :amount ) )"
            , nativeQuery = true)
    Page<Transaction> findAllTransactionByCriteria(@Param("transactionId") long transactionId,
                                                   @Param("playerId") long playerId,
                                                   @Param("type") String type,
                                                   @Param("amount") double amount,
                                                   Pageable page);

}
