### Usage:
1. Run at least two instances (can add `-Dserver.port=9090` to VM arguments)
2. navigate to index.html at `localhost:8080`
3. provide recipient name and connect to WebSocket
4. send a message
##### send notification message using curl
```bash
$ `curl -X POST http://localhost:8080/message -H "Content-Type: application/json" \
-d '{"orderNumber": "123", "message": "hello", "recipients": ["user", "group1"]}'`
```
