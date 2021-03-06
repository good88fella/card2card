package ru.sberbank.card2card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.sberbank.card2card.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDto {

    private String username;
    private String fullName;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFullName(user.getFullName());
        return userDto;
    }
}
