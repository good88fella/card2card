package ru.sberbank.card2card.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.card2card.dto.AuthRequestDto;
import ru.sberbank.card2card.dto.AuthResponseDto;
import ru.sberbank.card2card.dto.UserDto;
import ru.sberbank.card2card.dto.UserRegistrationDto;
import ru.sberbank.card2card.exceptions.ConfirmPasswordException;
import ru.sberbank.card2card.exceptions.UserAlreadyExistsException;
import ru.sberbank.card2card.model.User;
import ru.sberbank.card2card.security.jwt.JwtTokenProvider;
import ru.sberbank.card2card.service.UserService;

@RestController
@RequestMapping(value = "/api/auth/")
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null)
                throw new UsernameNotFoundException("User with username: " + username + " not found");

            String token = jwtTokenProvider.createToken(username);
            AuthResponseDto responseDto = new AuthResponseDto();
            responseDto.setUsername(username);
            responseDto.setToken(token);

            return ResponseEntity.ok(responseDto);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping(value = "registration")
    public ResponseEntity<UserDto> userRegistration(@RequestBody UserRegistrationDto requestDto) {
        try {
            if (userService.findByUsername(requestDto.getUsername()) != null)
                throw new UserAlreadyExistsException("User with username: " + requestDto.getUsername() + " already exist");

            if (!requestDto.getPassword().equals(requestDto.getConfirmPassword()))
                throw new ConfirmPasswordException("Passwords do not match");

            User user = requestDto.toUser();
            user = userService.register(user);

            return new ResponseEntity<>(UserDto.fromUser(user), HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            throw new UserAlreadyExistsException(e.getMessage());
        } catch (ConfirmPasswordException e) {
            throw new ConfirmPasswordException(e.getMessage());
        }
    }
}
