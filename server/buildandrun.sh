#!/bin/bash  
cd src
go build -o server . && ./server
cd ..
