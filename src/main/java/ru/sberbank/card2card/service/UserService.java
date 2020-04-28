package ru.sberbank.card2card.service;

import ru.sberbank.card2card.model.User;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByUsername(String username);
}
