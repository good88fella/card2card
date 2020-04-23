package ru.sberbank.card2card.dto;

import lombok.Data;

@Data
public class AddAmountRequestDto {

    private Long cardNumber;
    private Double amount;
}
