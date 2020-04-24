package ru.sberbank.card2card.dto;

import lombok.Data;

@Data
public class MoneyTransferDto {

    private Long fromCard;
    private Long toCard;
    private Double amount;
    private String confirm;
}
