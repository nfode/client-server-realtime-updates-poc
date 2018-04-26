# Client Server realtime updates
This is a proof of concept for using centrifugo to update clients in realtime if another client changes a value.
# How to run
## 1. Start the server side services
In the project root folder just run:
```
docker-compose up
```
## 2. Change the app properties
Change the following properties file to match the adresses of the host the server is running on
```
app/app/src/main/assets/application.properties
```
# Build the server
1. Enter the `server` folder
2. Run `./createdocker.sh`

# Run the server with docker
`docker run -d --net="host" -p 8080:8080 nfode/realtime-update-server:latest
`