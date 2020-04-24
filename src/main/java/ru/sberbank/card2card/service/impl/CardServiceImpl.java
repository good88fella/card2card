package ru.sberbank.card2card.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.card2card.exceptions.BankTransactionException;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;
import ru.sberbank.card2card.model.User;
import ru.sberbank.card2card.repository.CardRepository;
import ru.sberbank.card2card.service.CardService;
import ru.sberbank.card2card.service.OperationService;

import java.util.List;

@Service
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final OperationService operationService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, OperationService operationService) {
        this.cardRepository = cardRepository;
        this.operationService = operationService;
    }

    @Override
    public Card addCard(Card card) {
        if (!validCardNumber(card)) {
            log.warn("IN addCard - invalid card number: {}", card.getCardNumber());
            return null;
        }

        Card addedCard = cardRepository.save(card);
        log.info("IN addCard - card: {} successfully added", card);
        return addedCard;
    }

    @Override
    public Card findByCardNumber(Long cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber);

        if (card == null) {
            log.warn("IN findByCardNumber - no card found by card number: {}", cardNumber);
            return null;
        }

        log.info("IN findByCardNumber - card: {} found by card number: {}", card, cardNumber);
        return card;
    }

    @Override
    public Card addAmount(Long cardNumber, double amount) throws BankTransactionException {
        try {
            Card card = findByCardNumber(cardNumber);

            if (card == null)
                throw new BankTransactionException("Card: " + cardNumber + " not found");

            if (card.getBalance() + amount < 0)
                throw new BankTransactionException("Not enough money on card: " + cardNumber);

            card.setBalance(card.getBalance() + amount);
            cardRepository.save(card);
            log.info("IN addAmount - amount: {} added to card: {}", amount, card);
            return card;
        } catch (BankTransactionException e) {
            throw new BankTransactionException(e.getMessage());
        }
    }

    @Override
    public void sendMoney(Long fromCardNumber, Long toCardNumber, double amount) throws BankTransactionException {
        try {
            Card fromCard = addAmount(fromCardNumber, -amount);
            Card toCard = addAmount(toCardNumber, amount);
            log.info("IN sendMoney - amount: {} sent from card: {} to card: {}", amount, fromCard, toCard);
            saveOperation(fromCard, toCard, amount);
        } catch (BankTransactionException e) {
            throw new BankTransactionException(e.getMessage());
        }
    }

    @Override
    public List<Card> findAllByUser(User user) {
        List<Card> result = cardRepository.findAllByUser(user);

        if (result.size() == 0) {
            log.warn("IN findAllByUser - cards not found by user: {}", user);
            return null;
        }

        log.info("IN findAllByUser - cards: {} found by user: {}", result, user);
        return result;
    }

    private boolean validCardNumber(Card card) {
        String number = String.valueOf(card.getCardNumber());
        return number.length() == 16;
    }

    private void saveOperation(Card fromCard, Card toCard, double amount) {
        Operation operation = new Operation();
        operation.setSenderCard(fromCard);
        operation.setPayeeCard(toCard);
        operation.setTransferAmount(amount);
        operationService.saveOperation(operation);
    }
}
