# CameraServer

Photo management server with LDAP authentication and JWT authorization.

### Prerequisites
- Docker and Docker Compose installed.

### Start the Server
```bash
cd FancyCameraServer
docker compose up
# or run in background
docker compose up -d
Server available at http://localhost:8080
``` 

### Stop the Server
```bash
docker compose down
#remove all persistent data (photos + users)
docker compose down -v
```
### Usage
1. Register a User
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"1234"}'
  ```
2. Login
```bash
curl -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"1234"}'
```
3. Response
```bash
{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}
```
4. Upload a Photo
```bash
curl -X POST http://localhost:8080/api/photos/photo.jpg \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "image=@/path/to/photo.jpg"
  ```
5. List Photos
```bash
curl -X GET http://localhost:8080/api/photos \
  -H "Authorization: Bearer YOUR_TOKEN"
```
6. Download a Photo
```bash
curl -X GET http://localhost:8080/api/photos/photo.jpg \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o photo.jpg
```

### Notes
	-	LDAP authentication is handled by the OpenLDAP container.
	-	JWT tokens are used for session authorization.
	-	Both the server and LDAP service run together under Docker Compose with persistent volumes.