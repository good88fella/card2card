#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username admin --dbname card2card_db_test <<-EOSQL
  CREATE OR REPLACE FUNCTION update_changetimestamp_column()
    RETURNS TRIGGER AS
  \$\$
  BEGIN
    NEW.updated = NOW();
    RETURN NEW;
  END;
  \$\$ language 'plpgsql';

	CREATE TABLE IF NOT EXISTS users
	(
    		id              BIGSERIAL PRIMARY KEY,
    		username        VARCHAR(100) UNIQUE NOT NULL,
    		password        VARCHAR(256) NOT NULL,
    		full_name       VARCHAR(300) NOT NULL,
    		created         TIMESTAMP NOT NULL DEFAULT NOW(),
    		updated         TIMESTAMP NOT NULL DEFAULT NOW(),
    		status          VARCHAR(25) NOT NULL DEFAULT 'ACTIVE'
	);

  CREATE TRIGGER update_users_changetimestamp
    BEFORE UPDATE
    ON users
    FOR EACH ROW
  EXECUTE PROCEDURE
    update_changetimestamp_column();

  CREATE TABLE IF NOT EXISTS roles
	(
    		id              BIGSERIAL PRIMARY KEY,
    		name            VARCHAR(50) UNIQUE NOT NULL,
    		created         TIMESTAMP NOT NULL DEFAULT NOW(),
    		updated         TIMESTAMP NOT NULL DEFAULT NOW(),
    		status          VARCHAR(25) NOT NULL DEFAULT 'ACTIVE'
	);

  CREATE TRIGGER update_roles_changetimestamp
    BEFORE UPDATE
    ON roles
    FOR EACH ROW
  EXECUTE PROCEDURE
    update_changetimestamp_column();

	CREATE TABLE IF NOT EXISTS user_roles
	(
    		user_id         BIGINT REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    		role_id         BIGINT REFERENCES roles(id) ON DELETE CASCADE ON UPDATE RESTRICT
	);

	CREATE TABLE IF NOT EXISTS cards
	(
	      card_number     BIGINT PRIMARY KEY,
	      card_holder     BIGINT REFERENCES users(id),
	      balance         NUMERIC(10, 2) DEFAULT 0.00,
	      created         TIMESTAMP NOT NULL DEFAULT NOW(),
    		updated         TIMESTAMP NOT NULL DEFAULT NOW(),
    		status          VARCHAR(25) NOT NULL DEFAULT 'ACTIVE'
	);

  CREATE TRIGGER update_cards_changetimestamp
    BEFORE UPDATE
    ON cards
    FOR EACH ROW
  EXECUTE PROCEDURE
    update_changetimestamp_column();

	CREATE TABLE IF NOT EXISTS operations
	(
	      id              BIGSERIAL PRIMARY KEY,
	      sender_card     BIGINT REFERENCES cards(card_number),
	      payee_card      BIGINT REFERENCES cards(card_number),
	      transfer_amount NUMERIC(10, 2) NOT NULL,
	      created         TIMESTAMP NOT NULL DEFAULT NOW(),
    		updated         TIMESTAMP NOT NULL DEFAULT NOW(),
    		status          VARCHAR(25) NOT NULL DEFAULT 'ACTIVE'
	);

	CREATE TRIGGER update_operations_changetimestamp
    BEFORE UPDATE
    ON operations
    FOR EACH ROW
  EXECUTE PROCEDURE
    update_changetimestamp_column();
EOSQL
