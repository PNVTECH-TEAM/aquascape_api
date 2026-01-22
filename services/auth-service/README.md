# auth-service

Registration and login with JWT issuing. Publishes verification events to RabbitMQ.

## Build
```
./mvnw -B test -Dcommon-lib.repo.url=https://maven.pkg.github.com/YOUR_ORG/common-lib
```

## Run
```
./mvnw spring-boot:run -Dcommon-lib.repo.url=https://maven.pkg.github.com/YOUR_ORG/common-lib
```
