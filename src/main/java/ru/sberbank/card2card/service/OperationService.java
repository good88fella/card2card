package ru.sberbank.card2card.service;

import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;

import java.util.List;

public interface OperationService {

    Operation saveOperation(Operation operation);

    List<Operation> findByPayeeCard(Card card);

}
