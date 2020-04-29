package ru.sberbank.card2card.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ExceptionDto {

    private int statusCode;
    private String statusMsg;
    private String exceptionMsg;
    private String path;
    private Date date;

    public ExceptionDto(HttpStatus httpStatus, String msg, String path) {
        this.statusCode = httpStatus.value();
        this.statusMsg = httpStatus.name();
        this.exceptionMsg = msg;
        this.path = path;
        this.date = new Date();
    }
}
