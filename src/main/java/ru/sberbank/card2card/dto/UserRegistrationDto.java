package ru.sberbank.card2card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.sberbank.card2card.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserRegistrationDto {

    private String username;
    private String fullName;
    private String password;
    private String confirmPassword;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(password);
        return user;
    }
}
