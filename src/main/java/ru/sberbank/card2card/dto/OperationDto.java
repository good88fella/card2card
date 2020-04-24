package ru.sberbank.card2card.dto;

import lombok.Data;
import ru.sberbank.card2card.model.Operation;

import java.util.Date;

@Data
public class OperationDto {

    private Long id;
    private Long fromCard;
    private Long toCard;
    private Double transferAmount;
    private Date date;

    public static OperationDto fromOperation(Operation operation) {
        OperationDto operationDto = new OperationDto();
        operationDto.setId(operation.getId());
        operationDto.setFromCard(operation.getSenderCard().getCardNumber());
        operationDto.setToCard(operation.getPayeeCard().getCardNumber());
        operationDto.setTransferAmount(operation.getTransferAmount());
        operationDto.setDate(operation.getUpdated());
        return operationDto;
    }
}
