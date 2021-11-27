```bash
$ curl -X POST http://localhost:8080/message -H "Content-Type: application/json" \
-d '{"orderNumber": "123", "message": "hello", "recipients": ["user", ["group1"]]}'
```
