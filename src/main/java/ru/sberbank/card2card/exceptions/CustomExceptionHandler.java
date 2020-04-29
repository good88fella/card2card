package ru.sberbank.card2card.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sberbank.card2card.dto.ExceptionDto;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        ExceptionDto exceptionDto = new ExceptionDto(status, ex.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + " " + x.getDefaultMessage())
                .collect(Collectors.joining("\n")),
                ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(exceptionDto, headers, status);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({BankTransactionException.class, CardAlreadyExistsException.class, ConfirmPasswordException.class,
            InvalidCardNumberException.class, NotEnoughMoneyException.class, UserAlreadyExistsException.class})
    protected ExceptionDto handleConflict(RuntimeException ex, WebRequest request) {
        return new ExceptionDto(HttpStatus.CONFLICT, ex.getLocalizedMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler({CardNotFoundException.class})
    protected ExceptionDto handleNoContent(RuntimeException ex, WebRequest request) {
        return new ExceptionDto(HttpStatus.NO_CONTENT, ex.getLocalizedMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({JwtAuthenticationException.class})
    protected ExceptionDto handleForbidden(RuntimeException ex, WebRequest request) {
        return new ExceptionDto(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }
}
