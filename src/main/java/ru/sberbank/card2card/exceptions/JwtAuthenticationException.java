package ru.sberbank.card2card.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
