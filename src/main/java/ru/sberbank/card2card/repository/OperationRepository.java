package ru.sberbank.card2card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByPayeeCard(Card card);
}
