# Microservices Workspace (Poly-repo)

Workspace nay chua kien truc microservices tach ra tu monolith cu.
Moi service nam trong mot thu muc rieng duoi `services/` va duoc coi
la mot repo rieng khi day len GitHub.

## Cau truc thu muc
services/
services/
- ├─ auth-service/          # JWT auth, đăng ký/đăng nhập, publish event verify email
- ├─ user-service/          # Quản lý user + internal API
- ├─ notification-service/  # Verify token + gửi email, RabbitMQ consumer
- ├─ api-gateway/           # Spring Cloud Gateway
- ├─ common-lib/             # Thư viện dùng chung (constants, response DTO)
- ├─ _template-service/      # Template tạo service mới
- ├─ docker-compose.yml      # Chạy toàn bộ hệ thống local
- ├─ .env.example            # File mẫu biến môi trường
- └─ settings.example.xml    # File mẫu Maven + GitHub Packages

## Giai thich cac file co san (service repo)
Moi service deu co cac file sau:
- `pom.xml`: khai bao dependency va build Maven.
- `Dockerfile`: dong goi service thanh container.
- `mvnw`, `mvnw.cmd`, `.mvn/`: Maven Wrapper (chay build khong can cai Maven).
- `.github/workflows/maven.yml`: CI build/test tren GitHub Actions.
- `src/main/java/**`: ma nguon.
- `src/main/resources/application.yml`: cau hinh chay.
- `README.md`: huong dan rieng cho service.

## Yeu cau moi truong
- Java 21
- Docker + Docker Compose
- Moi service co Maven Wrapper (mvnw)

## Cach chay sau khi pull code
Co 2 cach: Docker Compose (khuyen dung) hoac chay local tung service.

### Cach 1: Docker Compose (khuyen dung)
```
cd services
copy .env.example .env
docker compose up --build
```

Truy cap:
- Gateway: http://localhost:8080
- RabbitMQ UI: http://localhost:15672

### Cach 2: Chay local tung service
Buoc 1: cai common-lib vao Maven local
```
cd services\\common-lib
./mvnw -DskipTests install
```

Buoc 2: chay tung service (moi service 1 terminal)
```
cd services\\user-service
./mvnw spring-boot:run

cd services\\auth-service
./mvnw spring-boot:run

cd services\\notification-service
./mvnw spring-boot:run

cd services\\api-gateway
./mvnw spring-boot:run
```

## GitHub Packages (common-lib)
`common-lib` publish len GitHub Packages theo tag `v*`.

De dung local:
1) Copy `services/settings.example.xml` -> `C:\\Users\\<you>\\.m2\\settings.xml`
2) Dien GitHub username va token (quyen `read:packages`)
3) Build voi repo URL:
```
./mvnw -B test -Dcommon-lib.repo.url=https://maven.pkg.github.com/YOUR_ORG/common-lib
```

## Bien moi truong
Docker Compose doc tu `services/.env` (copy tu `.env.example`).

Auth service:
- JWT_SECRET

RabbitMQ:
- RABBITMQ_USER, RABBITMQ_PASSWORD

User DB:
- USER_DB_USERNAME, USER_DB_PASSWORD, USER_DB_NAME

Notification DB:
- NOTIFY_DB_USERNAME, NOTIFY_DB_PASSWORD, NOTIFY_DB_NAME

Email:
- SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS

## Ports
- Gateway: 8080
- User service: 8081
- Auth service: 8082
- Notification service: 8083
- RabbitMQ: 5672 (AMQP), 15672 (UI)
- User DB: 5432
- Notification DB: 5432

## Flow verify email (tom tat)
1) Client goi `POST /api/auth/register` qua gateway.
2) Auth service tao user trong user-service (internal API).
3) Auth service publish event `user.verification` len RabbitMQ.
4) Notification service tao OTP, gui email.
5) User nhap OTP va goi `GET /api/verify/otp?email=...&otp=...` -> user-service update status.

## Quick test (curl)
Register:
```
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo\",\"email\":\"demo@example.com\",\"fullName\":\"Demo User\",\"password\":\"Abc123\"}"
```

Login:
```
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo\",\"password\":\"Abc123\"}"
```

Verify OTP:
```
curl "http://localhost:8080/api/verify/otp?email=demo@example.com&otp=123456"
```

Get users:
```
curl http://localhost:8080/api/users/all-users
```

## Huong dan tao service moi
Buoc 1: copy template
```
cd services
xcopy /E /I _template-service new-service
```

Buoc 2: cap nhat Maven
- `services/new-service/pom.xml`: doi `groupId`, `artifactId`, `name`
- Neu can `common-lib`, them dependency va version

Buoc 3: doi package va class
- `com.example.template` -> `pnvteck.<service>`
- `TemplateApplication` -> `<Service>Application`

Buoc 4: cap nhat `application.yml`
- `spring.application.name`
- `server.port` (khong trung port)
- Config DB/Rabbit/SMTP neu can

Buoc 5: them vao Docker Compose (neu can)
- Them service moi vao `services/docker-compose.yml`
- Neu can DB rieng, them Postgres container rieng

## Troubleshooting
- RabbitMQ chua san sang: doi vai giay hoac restart container.
- Postgres init loi: xoa volume va chay lai `docker compose up --build`.
- Login fail: kiem tra user da ACTIVE sau verify.
