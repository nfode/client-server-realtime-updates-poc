# Client Server realtime updates
This is a proof of concept for usin centrifugo to update clients in realtime if another client changes a value.
# Run centrifugo
1. Enter the `centrifugo` folder
2. Run 
    ```
    ./run.sh
    ```

# Build the server
1. Enter the `server` folder
2. Run `./createdocker.sh`

# Run the server with docker
`docker run -d --net="host" -p 8080:8080 realtime-update-server:latest
`


