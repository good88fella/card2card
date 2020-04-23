package ru.sberbank.card2card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.card2card.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
