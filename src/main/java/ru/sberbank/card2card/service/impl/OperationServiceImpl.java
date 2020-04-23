package ru.sberbank.card2card.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;
import ru.sberbank.card2card.repository.OperationRepository;
import ru.sberbank.card2card.service.OperationService;

import java.util.List;

@Service
@Slf4j
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    @Autowired
    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public Operation saveOperation(Operation operation) {
        Operation savedOperation = operationRepository.save(operation);
        log.info("IN saveOperation - operation: {} successfully saved", savedOperation);
        return savedOperation;
    }

    @Override
    public List<Operation> findByPayeeCard(Card card) {
        return null;
    }
}
