package ru.sberbank.card2card.rest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.card2card.dto.*;
import ru.sberbank.card2card.exceptions.BankTransactionException;
import ru.sberbank.card2card.exceptions.CardAlreadyExistsException;
import ru.sberbank.card2card.exceptions.CardNotFoundException;
import ru.sberbank.card2card.exceptions.InvalidCardNumberException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/card2card_db_test"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardRestControllerTest {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private PrepareTestDataBase prepareTestDataBase;
    @Autowired
    private PrepareTestTemplateHeaders prepareTestTemplateHeaders;

    private final String username = "user1";
    private final String password = "123456";

    @BeforeAll
    public void getRestTemplateWithAuthTokenInHeadersAndInsertValuesInDataBase() {
        prepareTestDataBase.insertValues();
        prepareTestTemplateHeaders.prepareHeaders(template, username, password);
    }

    @AfterAll
    public void clearHeadersAndDataBase() {
        prepareTestDataBase.clearTables();
        prepareTestTemplateHeaders.cleanHeaders(template);
    }

    @Test
    public void addCard_shouldCreated201() {
        Long cardNumber = 1000100010003333L;
        ResponseEntity<CardDto> responseEntity = template.postForEntity("/api/card", cardNumber, CardDto.class);
        CardDto actual = responseEntity.getBody();
        CardDto expected = new CardDto();
        expected.setCardNumber(cardNumber);
        expected.setUsername(username);
        expected.setBalance(0.00);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(expected, actual);
    }

    @Test
    public void addCardWithInvalidCardNumber_shouldConflict409() {
        Long cardNumber = 100010001000L;
        ResponseEntity<InvalidCardNumberException> responseEntity = template.postForEntity("/api/card", cardNumber,
                InvalidCardNumberException.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void addDuplicateCard() {
        Long cardNumber = 1000100010001001L;
        ResponseEntity<CardAlreadyExistsException> responseEntity = template.postForEntity("/api/card",
                cardNumber, CardAlreadyExistsException.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void showCardBalance_shouldOk200() {
        long cardNumber = 1000100010001001L;
        ResponseEntity<Double> responseEntity = template.getForEntity("/api/card/balance/" + cardNumber,
                Double.class);
        Double actual = responseEntity.getBody();
        Double expected = 1500.00;
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(expected, actual);
    }

    @Test
    public void showCardBalanceAnotherUser_shouldForbidden403() {
        long cardNumber = 1000100010001002L;
        ResponseEntity<BadCredentialsException> responseEntity = template.getForEntity("/api/card/balance/" + cardNumber,
                BadCredentialsException.class);
        String actual = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        String expected = "Access Denied";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(expected, actual);
    }

    @Test
    public void showCardBalanceNotRegisteredCard_shouldNoContent204() {
        long cardNumber = 1111100010001001L;
        ResponseEntity<CardNotFoundException> responseEntity = template.getForEntity("/api/card/balance/" + cardNumber,
                CardNotFoundException.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void addCorrectAmountToCorrectCard_shouldAccepted202() {
        long cardNumber = 1000100010001002L;
        AddAmountRequestDto requestDto = new AddAmountRequestDto();
        requestDto.setCardNumber(cardNumber);
        requestDto.setAmount(1000.00);
        ResponseEntity<CardDto> responseEntity = template.postForEntity("/api/card/balance",
                requestDto, CardDto.class);
        CardDto actual = responseEntity.getBody();
        CardDto expected = new CardDto();
        expected.setUsername("user2");
        expected.setCardNumber(cardNumber);
        expected.setBalance(2000.00);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.ACCEPTED);
        assertEquals(expected, actual);
    }

    @Test
    public void addIncorrectAmount_shouldConflict409() {
        long cardNumber = 1000100010001002L;
        AddAmountRequestDto requestDto = new AddAmountRequestDto();
        requestDto.setCardNumber(cardNumber);
        requestDto.setAmount(-1000.00);
        ResponseEntity<BankTransactionException> responseEntity = template.postForEntity("/api/card/balance",
                requestDto, BankTransactionException.class);
        String actual = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        String expected = "Incorrect amount";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(expected, actual);
    }

    @Test
    public void getInfoAboutCardHolder_shouldOk200() {
        long toCard = 1000100010001002L;
        ResponseEntity<String> responseEntity = template.getForEntity("/api/card/send-money/" + toCard, String.class);
        String actual = responseEntity.getBody();
        String expected = "Ivanov Ivan Ivanovich";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(expected, actual);
    }

    @Test
    public void sendMoney_shouldAccept202() {
        long fromCard = 1000100010001001L;
        long toCard = 1000100010001002L;
        MoneyTransferDto transferDto = new MoneyTransferDto();
        transferDto.setFromCard(fromCard);
        transferDto.setToCard(toCard);
        transferDto.setAmount(100.00);
        transferDto.setConfirm("OK");

        ResponseEntity<String> responseEntity = template.postForEntity("/api/card/send-money/confirm", transferDto, String.class);
        String actual = responseEntity.getBody();
        String expected = "Operation completed successfully";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.ACCEPTED);
        assertEquals(expected, actual);
    }

    @Test
    public void sendMoneyMoreThenOnCard_shouldConflict409() {
        long fromCard = 1000100010001001L;
        long toCard = 1000100010001002L;
        MoneyTransferDto transferDto = new MoneyTransferDto();
        transferDto.setFromCard(fromCard);
        transferDto.setToCard(toCard);
        transferDto.setAmount(10000.00);
        transferDto.setConfirm("OK");

        ResponseEntity<BankTransactionException> responseEntity = template.postForEntity("/api/card/send-money/confirm", transferDto,
                BankTransactionException.class);
        String actual = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        String expected = "Not enough money on card: " + fromCard;
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(expected, actual);
    }

    @Test
    public void sendMoneyWithOutConfirm_shouldConflict409() {
        long fromCard = 1000100010001001L;
        long toCard = 1000100010001002L;
        MoneyTransferDto transferDto = new MoneyTransferDto();
        transferDto.setFromCard(fromCard);
        transferDto.setToCard(toCard);
        transferDto.setAmount(100.00);
        transferDto.setConfirm("NO");

        ResponseEntity<BankTransactionException> responseEntity = template.postForEntity("/api/card/send-money/confirm", transferDto,
                BankTransactionException.class);
        String actual = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        String expected = "Operation denied";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(expected, actual);
    }

    @Test
    public void sendMoneyFromCardNotAuthUser_shouldForbidden403() {
        long fromCard = 1000100010001002L;
        long toCard = 1000100010001001L;
        MoneyTransferDto transferDto = new MoneyTransferDto();
        transferDto.setFromCard(fromCard);
        transferDto.setToCard(toCard);
        transferDto.setAmount(100.00);
        transferDto.setConfirm("OK");

        ResponseEntity<BadCredentialsException> responseEntity = template.postForEntity("/api/card/send-money/confirm", transferDto,
                BadCredentialsException.class);
        String actual = Objects.requireNonNull(responseEntity.getBody()).getMessage();
        String expected = "Access Denied";
        assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(expected, actual);
    }
}