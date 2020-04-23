NAME=card2card_db

build:
	docker build -t ${USER}/${NAME} .

run:
	docker run -d -e POSTGRES_PASSWORD=pass --name ${NAME} -p 5432:5432 ${USER}/${NAME}
