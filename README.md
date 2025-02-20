![image](https://github.com/user-attachments/assets/d21a0d22-565d-4fbf-aac4-eb5b65449956)

## 1️⃣ **프로젝트 개요**

- **프로젝트명:** Chalcak
- **설명:** 사진의 설정값, 촬영 방법 등을 공유하며 소통하는 사진 커뮤니티
- **주요 기능:**
    - 사용자 회원가입 및 로그인 (JWT 인증)
    - 게시글 작성 및 관리 (CRUD)
    - 사진 업로드 (AWS S3 연동)
    - 댓글 작성 및 실시간 반영

## 2️⃣ **기술 스택**

| 분야 | 기술 |
| --- | --- |
| **프론트엔드** | HTML, JavaScript, CSS |
| **백엔드** | Express(Node.js) → Spring Boot (마이그레이션) |
| **데이터베이스** | MySQL (JDBC 사용) |
| **파일 저장** | AWS S3 |
| **인증** | JWT (JSON Web Token) |

## 3️⃣ **프로젝트 개발 과정**

✅ **초기 설계 & 개발**

- Express와 Node.js 기반으로 간단한 REST API 서버 개발
- HTML, CSS, JavaScript를 활용한 UI 구축

✅ **백엔드 마이그레이션**

- Express에서 Spring Boot로 변경
- MySQL + JDBC로 데이터 관리 방식 변경
- API 구조 개선 및 엔드포인트 정리

✅ **기능 개선 & 최적화**

- AWS S3 연동하여 파일 저장 방식 개선
- JWT 인증 적용으로 보안 강화
- 댓글 실시간 반영을 위한 fetch API 활용

## 4️⃣ **핵심 기능 & 차별점**

🚀 **사진 업로드 & 저장**

- 사용자가 이미지를 업로드하면 **AWS S3에 저장**하고, DB에는 URL만 저장

💬 **실시간 댓글 기능**

- fetch API를 활용하여 댓글 등록 시 즉시 UI에 반영


## 5️⃣ **결과 및 배운 점**

✅ **백엔드 마이그레이션 경험**을 통해 RESTful API 설계 및 서버 확장성을 이해함

✅ 클라이언트-서버 간 **비동기 통신(fetch API, async/await)**을 경험하며 동작 방식 학습

✅ **실무에서 많이 사용하는 AWS S3, JWT 인증, MySQL 연동** 등을 직접 구현해봄
