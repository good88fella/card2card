package ru.sberbank.card2card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.card2card.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String login);
}
