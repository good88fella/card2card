package ru.sberbank.card2card.rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.card2card.dto.OperationDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/card2card_db_test"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationHistoryRestControllerTest {

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
    public void getOperationHistoryOrderedByTransferAmountAsc_shouldOk200() {
        ResponseEntity<OperationDto[]> responseEntity = template.getForEntity("/api/operations/history/sum?order=asc",
                OperationDto[].class);
        OperationDto[] actual = responseEntity.getBody();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assert actual != null;
        assertEquals(3, actual.length);
        assertEquals(actual[0].getTransferAmount(), 100);
        assertEquals(actual[1].getTransferAmount(), 500);
        assertEquals(actual[2].getTransferAmount(), 600);
    }

    @Test
    public void getOperationHistoryOrderedByTransferAmountDesc_shouldOk200() {
        ResponseEntity<OperationDto[]> responseEntity = template.getForEntity("/api/operations/history/sum?order=desc",
                OperationDto[].class);
        OperationDto[] actual = responseEntity.getBody();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assert actual != null;
        assertEquals(3, actual.length);
        assertEquals(actual[0].getTransferAmount(), 600);
        assertEquals(actual[1].getTransferAmount(), 500);
        assertEquals(actual[2].getTransferAmount(), 100);
    }
}