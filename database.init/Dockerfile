FROM	postgres:12.2
COPY	00_init.sh /docker-entrypoint-initdb.d/
COPY	01_create_tables.sh /docker-entrypoint-initdb.d/
COPY    02_insert_test_values.sh /docker-entrypoint-initdb.d/