package ru.sberbank.card2card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByPayeeCard(Card card);

    @Query(nativeQuery = true,
            value = "SELECT * FROM operations op WHERE op.sender_card = ?1 OR op.payee_card = ?1")
    List<Operation> findAllByCardNumber(Long card);
}
