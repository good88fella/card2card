#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER admin WITH PASSWORD 'pass';
    CREATE DATABASE card2card_db;
    CREATE DATABASE card2card_db_test;
    GRANT ALL PRIVILEGES ON DATABASE card2card_db, card2card_db_test TO admin;
EOSQL
