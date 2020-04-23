#!/bin/bash
# user1 : 123456
# user2 : 111111
# user3 : qwerty
set -e
psql -v ON_ERROR_STOP=1 --username admin --dbname card2card_db <<-EOSQL
	INSERT INTO users(username, password, full_name) VALUES('user1', '\$2a\$04\$4VGnVRjYgEThaQpyntRLg.1WsdtBHQaIZ6d6HLcg95hJw5daXXc1G', 'Userov User Userovich');
	INSERT INTO users(username, password, full_name) VALUES('user2', '\$2a\$04\$opmA33aTLYlR.M4QYrXoOu3rkOSyXjVeCRx.6IE7VmWMGEMt0fDSq', 'Ivanov Ivan Ivanovich');
	INSERT INTO users(username, password, full_name) VALUES('user3', '\$2a\$04\$1E6CnNp/y/FMAJjkEtDp/.MhuoGspMrvCqNG1CtrUHgK2jh2nz/M.', 'Petrov Petr Petrovich');

	INSERT INTO roles(name) VALUES('ROLE_USER');
	INSERT INTO roles(name) VALUES('ROLE_ADMIN');

	INSERT INTO user_roles(user_id, role_id) VALUES(1, 1);
	INSERT INTO user_roles(user_id, role_id) VALUES(2, 1);
	INSERT INTO user_roles(user_id, role_id) VALUES(3, 1);

	INSERT INTO cards(card_number, card_holder, balance) VALUES(1000100010001001, 1, 1500);
	INSERT INTO cards(card_number, card_holder, balance) VALUES(1000100010001002, 2, 1000);
	INSERT INTO cards(card_number, card_holder, balance) VALUES(1000100010001003, 3, 10500);

  INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(1000100010001001, 1000100010001003, 500);
  INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(1000100010001003, 1000100010001002, 1000);
  INSERT INTO operations(sender_card, payee_card, transfer_amount) VALUES(1000100010001002, 1000100010001001, 500);
EOSQL