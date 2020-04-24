package ru.sberbank.card2card.service;

import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;

import java.util.List;
import java.util.Set;

public interface OperationService {

    Operation saveOperation(Operation operation);

    List<Operation> findAllByCardNumber(Long card);
}
