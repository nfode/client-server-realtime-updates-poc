#!/bin/bash
docker run -d --ulimit nofile=65536:65536 -v $(pwd):/centrifugo -p 8000:8000 centrifugo/centrifugo centrifugo -c config.json
