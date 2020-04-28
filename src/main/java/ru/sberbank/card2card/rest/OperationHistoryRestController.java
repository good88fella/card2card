package ru.sberbank.card2card.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.card2card.dto.OperationDto;
import ru.sberbank.card2card.exceptions.CardNotFoundException;
import ru.sberbank.card2card.model.Card;
import ru.sberbank.card2card.model.Operation;
import ru.sberbank.card2card.model.User;
import ru.sberbank.card2card.service.CardService;
import ru.sberbank.card2card.service.OperationService;
import ru.sberbank.card2card.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/api/operations/history/")
public class OperationHistoryRestController {

    private final OperationService operationService;
    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public OperationHistoryRestController(OperationService operationService, CardService cardService, UserService userService) {
        this.operationService = operationService;
        this.cardService = cardService;
        this.userService = userService;
    }

    @GetMapping(value = "sum")
    public ResponseEntity<List<OperationDto>> getHistoryByTheAmountOfTransfers(@RequestParam String order) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(loggedInUsername);
        List<Card> userCards = cardService.findAllByUser(user);

        if (userCards == null)
            throw new CardNotFoundException("User: " + user.getUsername() + " has no cards");

        List<OperationDto> result = new ArrayList<>();
        for (Card card : userCards) {
            List<Operation> operations = operationService.findAllByCardNumber(card.getCardNumber());

            if (operations == null)
                return new ResponseEntity<>(HttpStatus.OK);

            for (Operation operation : operations)
                result.add(OperationDto.fromOperation(operation));
        }
        if (order.equalsIgnoreCase("ASC"))
            result.sort(Comparator.comparing(OperationDto::getTransferAmount));
        else if (order.equalsIgnoreCase("DESC"))
            result.sort(Comparator.comparing(OperationDto::getTransferAmount).reversed());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
