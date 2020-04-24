# CARD2CARD<br>

## Описание<br>
Реализованы:<br>
1. Основная и тестовая базы данных Postgres в docker контейнере.<br>
2. Регистрация, авторизация и аунтефикация пользователей.<br>
3. Добавление карт и просмотр их баланса.<br>
4. Пополнение баланса карта.<br>
5. Перевод средст на карты других клиентов.<br>
Для указанного функционала написаны тесты.<br>
## Initialize application database and test database<br>
Для развертывания базы данных необходим docker daemon.<br>
cd database.init<br>
make all<br>

## Start application<br>
./gradlew clean build<br>
java -jar build/libs/card2card-0.0.1-SNAPSHOT.jar<br>