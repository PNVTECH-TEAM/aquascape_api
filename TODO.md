# TODO: Fix Registration and Login Features

## Tasks
- [x] Add missing dependency injections in AuthServiceImpl.java (EmailService and JwtProvider)
- [x] Add missing dependency injections in AuthController.java (VerificationTokenRepository and EmailService)
- [x] Move JwtProvider from security package to auth package to fix import mismatch
- [x] Add missing methods in UserRepository (findByUsername, findByEmail) and import Optional
- [x] Test the application to ensure registration and login work
