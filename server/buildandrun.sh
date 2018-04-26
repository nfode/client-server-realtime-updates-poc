#!/bin/bash  
export CENTRIFUGO_HOST="localhost"
export CENTRIFUGO_PORT="8000"
go build -o server . && ./server
