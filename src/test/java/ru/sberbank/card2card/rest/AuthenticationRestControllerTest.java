package ru.sberbank.card2card.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.sberbank.card2card.dto.AuthRequestDto;
import ru.sberbank.card2card.dto.AuthResponseDto;
import ru.sberbank.card2card.dto.UserDto;
import ru.sberbank.card2card.dto.UserRegistrationDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationRestControllerTest {

    @Autowired
    private TestRestTemplate template;

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
    public void userRegistration_shouldCreate201() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("user100");
        userRegistrationDto.setFullName("FIO");
        userRegistrationDto.setPassword("111111");
        userRegistrationDto.setConfirmPassword("111111");
        ResponseEntity<UserDto> result = template.postForEntity("/api/auth/registration", userRegistrationDto, UserDto.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(result.getBody(), UserDto.fromUser(userRegistrationDto.toUser()));
    }
}