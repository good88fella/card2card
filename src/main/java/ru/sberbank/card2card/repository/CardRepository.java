package ru.sberbank.card2card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.card2card.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardNumber(Long cardNumber);
}
