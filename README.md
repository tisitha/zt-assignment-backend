## Environment Variables

````
SERVER_PORT=8080

DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

JPA_HIBERNATE=org.hibernate.dialect.PostgreSQLDialect
JPA_DDL_AUTO=

JWT_SECRET=

MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=

MAIL_EMAIL=

FRONTEND_URL=https://EXAMPLE.com/

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
````

---

## Prerequisites

- Java Development Kit (JDK) 21
- Maven: Version 3.6.3+
- Git

---

## Setup instructions

```
git clone https://github.com/tisitha/zt-assignment-backend.git
```
```
cd zt-assignment-backend
```
```
./mvnw clean package
```
```
./mvnw spring-boot:run
```

---

## Useful APIs

- User registration
```
curl --location '${HOST}/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fname": ${"FIRST_NAME"},
    "lname": ${"LAST_NAME"},
    "email": ${"EMAIL"},
    "password": ${"PASSWORD"},
    "passwordRepeat": ${"PASSWORD_REPEAT"}
}'
```
- User login
```
curl --location '${HOST}/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": ${"EMAIL"},
    "password": ${"PASSWORD"}
}'
```
- User profile information
```
curl --location '${HOST}/api/user/profile' \
--header 'Authorization: Bearer ${ACCESS_TOKEN}'
```