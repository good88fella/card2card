package ru.sberbank.card2card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.sberbank.card2card.model.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CardDto {

    private Long cardNumber;
    private String username;
    private Double balance;

    public static CardDto fromCard(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setUsername(card.getUser().getUsername());
        card.setBalance(card.getBalance());
        return cardDto;
    }
}
