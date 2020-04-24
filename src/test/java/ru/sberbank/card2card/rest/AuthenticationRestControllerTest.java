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
import ru.sberbank.card2card.dto.*;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationRestControllerTest {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private PrepareTestDataBase prepareTestDataBase;

    @BeforeAll
    public void insertValuesInDataBase() {
        prepareTestDataBase.insertValues();
    }

    @AfterAll
    public void clearDataBase() {
        prepareTestDataBase.clearTables();
    }

    @Test
    public void correctUserLoginAndPasswordAuth_shouldSucceedWith200() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("user1");
        requestDto.setPassword("123456");
        ResponseEntity<AuthResponseDto> result = template.postForEntity("/api/auth/login", requestDto, AuthResponseDto.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void incorrectUserLoginAuth_shouldForbidden403() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("user11");
        requestDto.setPassword("123456");
        ResponseEntity<AuthResponseDto> result = template.postForEntity("/api/auth/login", requestDto, AuthResponseDto.class);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void incorrectUserPasswordAuth_shouldForbidden403() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("user1");
        requestDto.setPassword("1234567");
        ResponseEntity<AuthResponseDto> result = template.postForEntity("/api/auth/login", requestDto, AuthResponseDto.class);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void duplicateUsernameRegistration_shouldConflict409() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("user1");
        userRegistrationDto.setFullName("Userov User Userovich");
        userRegistrationDto.setPassword("123456");
        userRegistrationDto.setConfirmPassword("123456");
        ResponseEntity<UserDto> result = template.postForEntity("/api/auth/registration", userRegistrationDto, UserDto.class);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void passwordsDoNotMatchRegistration_shouldConflict409() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("user100");
        userRegistrationDto.setFullName("FIO");
        userRegistrationDto.setPassword("111111");
        userRegistrationDto.setConfirmPassword("1111112");
        ResponseEntity<UserDto> result = template.postForEntity("/api/auth/registration", userRegistrationDto, UserDto.class);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void correctUserRegistration_shouldCreate201() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("user100");
        userRegistrationDto.setFullName("FIO");
        userRegistrationDto.setPassword("111111");
        userRegistrationDto.setConfirmPassword("111111");
        ResponseEntity<UserDto> result = template.postForEntity("/api/auth/registration", userRegistrationDto, UserDto.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(result.getBody(), UserDto.fromUser(userRegistrationDto.toUser()));
    }

    @Test
    public void requestOnPrivateServiceWithOutAuthenticationToken_shouldForbidden403() {
        Long cardNumber = 1000100010005555L;
        ResponseEntity<CardDto> responseEntity = template.postForEntity("/api/card/add", cardNumber, CardDto.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }
}