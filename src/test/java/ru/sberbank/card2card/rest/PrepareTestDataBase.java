package ru.sberbank.card2card.rest;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PrepareTestDataBase {

    @PersistenceContext
    private EntityManager entityManager;

    // user1 : 123456
    // user2 : 111111
    // user3 : qwerty
    @Transactional
    public void insertValues() {
        entityManager.createNativeQuery("INSERT INTO users(username, password, full_name) VALUES(?, ?, ?)")
                .setParameter(1, "user1")
                .setParameter(2, "$2a$04$4VGnVRjYgEThaQpyntRLg.1WsdtBHQaIZ6d6HLcg95hJw5daXXc1G")
                .setParameter(3, "Userov User Userovich")
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO users(username, password, full_name) VALUES(?, ?, ?)")
                .setParameter(1, "user2")
                .setParameter(2, "$2a$04$opmA33aTLYlR.M4QYrXoOu3rkOSyXjVeCRx.6IE7VmWMGEMt0fDSq")
                .setParameter(3, "Ivanov Ivan Ivanovich")
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO users(username, password, full_name) VALUES(?, ?, ?)")
                .setParameter(1, "user3")
                .setParameter(2, "$2a$04$1E6CnNp/y/FMAJjkEtDp/.MhuoGspMrvCqNG1CtrUHgK2jh2nz/M.")
                .setParameter(3, "Petrov Petr Petrovich")
                .executeUpdate();

        entityManager.createNativeQuery("INSERT INTO roles(name) VALUES(?)")
                .setParameter(1, "ROLE_USER")
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO roles(name) VALUES(?)")
                .setParameter(1, "ROLE_ADMIN")
                .executeUpdate();

        entityManager.createNativeQuery("INSERT INTO user_roles(user_id, role_id) VALUES(?, ?)")
                .setParameter(1, 1)
                .setParameter(2, 1)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO user_roles(user_id, role_id) VALUES(?, ?)")
                .setParameter(1, 2)
                .setParameter(2, 1)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO user_roles(user_id, role_id) VALUES(?, ?)")
                .setParameter(1, 3)
                .setParameter(2, 1)
                .executeUpdate();

        entityManager.createNativeQuery("INSERT INTO cards(card_number, card_holder, balance) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001001L)
                .setParameter(2, 1)
                .setParameter(3, 1500)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO cards(card_number, card_holder, balance) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001002L)
                .setParameter(2, 2)
                .setParameter(3, 1000)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO cards(card_number, card_holder, balance) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001003L)
                .setParameter(2, 3)
                .setParameter(3, 10500)
                .executeUpdate();

        entityManager.createNativeQuery("INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001001L)
                .setParameter(2, 1000100010001003L)
                .setParameter(3, 500)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001003L)
                .setParameter(2, 1000100010001002L)
                .setParameter(3, 1000)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(?, ?, ?)")
                .setParameter(1, 1000100010001002L)
                .setParameter(2, 1000100010001001L)
                .setParameter(3, 500)
                .executeUpdate();
    }

    @Transactional
    public void clearTables() {
        entityManager.createNativeQuery("TRUNCATE users, roles, user_roles, cards, operations RESTART IDENTITY CASCADE")
                .executeUpdate();
    }
}
