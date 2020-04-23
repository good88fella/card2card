package ru.sberbank.card2card.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.card2card.dto.AddAmountRequestDto;
import ru.sberbank.card2card.dto.CardDto;
import ru.sberbank.card2card.dto.MoneyTransferDto;
import ru.sberbank.card2card.exceptions.BankTransactionException;
import ru.sberbank.card2card.exceptions.CardAlreadyExistsException;
import ru.sberbank.card2card.exceptions.CardNotFoundException;
import ru.sberbank.card2card.exceptions.InvalidCardNumberException;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.service.CardService;
import ru.sberbank.card2card.service.UserService;

@RestController
@RequestMapping(value = "/api/card/")
public class CardRestController {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public CardRestController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @PostMapping(value = "add")
    public ResponseEntity<CardDto> addCard(@RequestBody Long cardNumber) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            if (cardService.findByCardNumber(cardNumber) != null)
                throw new CardAlreadyExistsException("Card with card number: " + cardNumber + " already added");

            Card card = new Card();
            card.setCardNumber(cardNumber);
            card.setUser(userService.findByUsername(loggedInUsername));

            card = cardService.addCard(card);

            if (card == null)
                throw new InvalidCardNumberException("Incorrect card number: " + cardNumber);

            return new ResponseEntity<>(CardDto.fromCard(card), HttpStatus.CREATED);
        } catch (CardAlreadyExistsException e) {
            throw new CardAlreadyExistsException(e.getMessage());
        } catch (InvalidCardNumberException e) {
            throw new InvalidCardNumberException(e.getMessage());
        }
    }

    @GetMapping(value = "balance/{card_number}")
    public ResponseEntity<Double> showCardBalance(@PathVariable(name = "card_number") Long cardNumber) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Card card = cardService.findByCardNumber(cardNumber);

            if (card == null)
                throw new CardNotFoundException("Card: " + cardNumber + " not found");

            if (!loggedInUsername.equals(card.getUser().getUsername()))
                throw new BadCredentialsException("User: " + loggedInUsername + " is not a holder of card number: " + cardNumber);

            return new ResponseEntity<>(card.getBalance(), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (CardNotFoundException e) {
            throw new CardNotFoundException(e.getMessage());
        }
    }

    @PutMapping(value = "balance/add_amount")
    public ResponseEntity<CardDto> addAmountToCard(@RequestBody AddAmountRequestDto requestDto) throws BankTransactionException {
        try {
            if (requestDto.getAmount() <= 0)
                throw new IllegalArgumentException("Incorrect amount");

            Card card = cardService.addAmount(requestDto.getCardNumber(), requestDto.getAmount());

            return new ResponseEntity<>(CardDto.fromCard(card), HttpStatus.ACCEPTED);
        } catch (BankTransactionException e) {
            throw new BankTransactionException(e.getMessage());
        }
    }

    @PutMapping(value = "send_money")
    public ResponseEntity<String> moneyTransfer(@RequestBody MoneyTransferDto transferDto) throws BankTransactionException {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Card card = cardService.findByCardNumber(transferDto.getFromCard());
            if (card == null)
                throw new BankTransactionException("Card: " + transferDto.getFromCard() + " not found");

            if (!loggedInUsername.equals(card.getUser().getUsername()))
                throw new BadCredentialsException("User: " + loggedInUsername + " is not a holder of card number: " + transferDto.getFromCard());

            cardService.sendMoney(transferDto.getFromCard(), transferDto.getToCard(), transferDto.getAmount());
            return new ResponseEntity<>("Operation completed successfully\n", HttpStatus.ACCEPTED);
        } catch (BankTransactionException e) {
            throw new BankTransactionException(e.getMessage());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }
}
