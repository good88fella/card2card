package ru.sberbank.card2card.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidCardNumberException extends RuntimeException {

    public InvalidCardNumberException(String message) {
        super(message);
    }
}
