package ru.sberbank.card2card.dto;

import lombok.Data;

@Data
public class AuthResponseDto {

    private String username;
    private String token;
}
