#!/bin/bash
NAME=card2card_db
docker stop ${NAME}
docker rm ${NAME}
docker rmi ${USER}/${NAME}
