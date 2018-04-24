#!/bin/bash  

CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o main
docker build -t realtime-update-server .
rm -f main
