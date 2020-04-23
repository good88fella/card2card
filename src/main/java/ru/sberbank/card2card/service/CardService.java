package ru.sberbank.card2card.service;

import ru.sberbank.card2card.exceptions.BankTransactionException;
import ru.sberbank.card2card.model.Card;

public interface CardService {

    Card addCard(Card card);

    Card findByCardNumber(Long cardNumber);

    Card addAmount(Long cardNumber, double amount) throws BankTransactionException;

    void sendMoney(Long fromCardNumber, Long toCardNumber, double amount) throws BankTransactionException;
}
