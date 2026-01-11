project-root
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
│
└── src
    └── main
        ├── java
        │   └── com/company/project
        │       │
        │       ├── ProjectApplication.java
        │       │
        │       ├── user
        │       │   ├── UserController.java
        │       │   ├── UserService.java
        │       │   ├── UserServiceImpl.java
        │       │   ├── UserRepository.java
        │       │   ├── UserEntity.java
        │       │   │
        │       │   └── dto
        │       │        ├── UserRequest.java
        │       │        └── UserResponse.java
        │       │
        │       ├── product
        │       │   ├── ProductController.java
        │       │   ├── ProductService.java
        │       │   ├── ProductServiceImpl.java
        │       │   ├── ProductRepository.java
        │       │   ├── ProductEntity.java
        │       │   │
        │       │   └── dto
        │       │        ├── ProductRequest.java
        │       │        └── ProductResponse.java
        │       │
        │       └── common
        │           ├── config
        │           │    └── AppConfig.java
        │           │
        │           ├── exception
        │           │    ├── GlobalExceptionHandler.java
        │           │    └── CustomException.java
        │           │
        │           ├── util
        │           │    └── DateUtil.java
        │           │
        │           ├── constant
        │           │    └── AppConstant.java
        │           │
        │           └── response
        │                └── ApiResponse.java
        │
        └── resources
            ├── application.yml
            ├── static
            └── templates


# All-in-One (Spring Boot)

Huong dan nhanh de chay du an tren may local hoac bang Docker Compose.

## Yeu cau
- Java 21 (neu chay local)
- Docker + Docker Compose (neu chay bang container)

## Cau hinh moi truong
Du an dung file `.env`. Mau co san:

```
DB_HOST=localhost
DB_USERNAME=root
DB_PASSWORD=123456

MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=all_in_one_db

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email@gmail.com
SMTP_PASS=your_app_password
SMTP_FROM=your_email@gmail.com

SERVER_PORT=8080
```

Luu y:
- `SMTP_PASS` nen la Gmail App Password (16 ky tu).
- Khi chay Docker, `DB_HOST` se duoc override thanh `db` trong `docker-compose.yml`.

## Chay bang Docker Compose
```
docker compose up --build
```

Ung dung chay tren: `http://localhost:8080`

## Chay local (khong Docker)
```
./mvnw spring-boot:run
```

Neu dung Windows:
```
mvnw.cmd spring-boot:run
```

## Chay bang Maven (neu da cai Maven)
```
mvn spring-boot:run
```

Hoac build va chay file jar:
```
mvn clean package -DskipTests
java -jar target/all-in-one-0.0.1-SNAPSHOT.jar
```

## Kiem tra nhanh
- Server mac dinh: `http://localhost:8080`
- CSDL MySQL mac dinh: `localhost:3306` (neu chay local), `db:3306` (trong Docker)

## Thu muc quan trong
- `src/main/java`: ma nguon
- `src/main/resources/application.yml`: cau hinh
- `docker-compose.yml`: cau hinh Docker
