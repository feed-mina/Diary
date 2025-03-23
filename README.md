# Diary & Authentication Web App

## 📌 프로젝트 개요
이 프로젝트는 **Vue.js**와 **Spring Boot**를 사용하여 **일기장 기능 및 사용자 인증**(일반 로그인 & 카카오 소셜 로그인)을 제공하는 웹 애플리케이션입니다.

사용자는 **일기장**에 글을 작성하고 수정할 수 있으며, **JWT 및 이메일 인증**을 포함한 **일반 로그인과 카카오 소셜 로그인**을 통해 회원가입 및 로그인이 가능합니다.

---

## 🚀 주요 기능
### 📒 일기장 기능
- **CRUD 기능:** 글 작성 / 수정 / 삭제 / 조회
- **사용자별 일기 관리:** 내가 쓴 일기만 보기, 전체 일기 보기, 로그인한 사용자가 자신의 일기 작성 및 조회 가능
- **MySQL 데이터베이스 저장**

### 🔐 사용자 인증 (Authentication)
1. **일반 로그인 (JWT + 이메일 인증 포함)**  
   - JWT (JSON Web Token) 기반 로그인  
   - 이메일 인증 (SMTP를 활용한 인증 코드 발송)  
   - 비밀번호 암호화 (BCrypt 적용)  
2. **카카오 소셜 로그인**  
   - OAuth 2.0 기반 카카오 로그인  
   - 카카오 프로필 정보 받아오기 (닉네임, 이메일 등)  
   - 기존 회원과 연동하여 JWT 발급  

---

## 🛠 기술 스택
### ✅ Frontend (Vue.js)
- Vue 3
- Vue Router (페이지 라우팅)
- 로컬 스토리지 기반 상태 관리 (`onMounted` + `watch` 활용)
- 로그인 유무 체크
- Axios (백엔드 API 호출, `axios.interceptor` 적용)

### ✅ Backend (Spring Boot)
- Spring Boot 3
- Spring Security (인증 및 보안)
- MyBatis (데이터베이스 연동)
- JWT (토큰 인증)
- SMTP (이메일 인증)
- OAuth 2.0 (카카오 로그인)
- MySQL (사용자 정보 및 일기 데이터 저장)

---

## 📂 프로젝트 구조
```
my-project
├── Ai-Diary-server (Spring Boot)
│    ├── src/main/java/com/domain/demo_backend/controller  (API 컨트롤러)
│    ├── src/main/java/com/domain/demo_backend/service     (비즈니스 로직)
│    ├── src/main/java/com/domain/demo_backend/user       (계정 dto/domain)
│    ├── src/main/java/com/domain/demo_backend/diary      (다이어리 dto/domain)
│    ├── src/main/java/com/domain/demo_backend/util       (유틸리티)
│    ├── src/main/java/com/domain/demo_backend/mapper     (데이터베이스 연동)
│    ├── src/main/resources/application.properties        (설정 파일)
│    ├── src/main/resources/mappers                      (마이바티스)
│
├── frontend (Vue.js)
│    ├── src/components   (컴포넌트)
│    ├── src/page         (페이지 설정)
│    ├── src/router       (라우터 설정)
│    ├── src/store        (상태 관리 - 로컬 스토리지 활용)
│    ├── src/App.vue      (axios.interceptor 적용)
│
├── README.md
```

---

## 📌 API 명세
### 🔑 사용자 인증 API
| Method | Endpoint | 설명 |
|--------|------------------------|--------------------------------|
| POST   | `/api/auth/register`   | 일반 회원가입 (이메일 인증 포함) |
| POST   | `/api/auth/login`      | 일반 로그인 (JWT 발급) |
| GET    | `/api/auth/verify-email?token=xxx` | 이메일 인증 확인 |
| POST   | `/api/kakao/login`     | 카카오 로그인 |

### 📝 일기장 API
| Method | Endpoint | 설명 |
|--------|----------------------------|------------------|
| POST   | `/api/diary/addDiaryList`  | 일기 작성 |
| GET    | `/api/diary/viewDiarylist` | 일기 목록 조회 |
| GET    | `/api/diary/viewDiaryItem/{diaryId}` | 일기 상세보기 |

### ✔ 일반 로그인 요청 예시
```json
{
  "email": "user@domain/demo_backend.com",
  "password": "securepassword"
}
```

### ✔ 카카오 로그인 요청 예시
```json
{
  "accessToken": "kakao_access_token"
}
```

### ✔ 응답 예시 (JWT 포함)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 🛠 axios.interceptor 적용 (App.vue)
```javascript
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem("jwtToken") || localStorage.getItem("kakaoToken");
    console.log("@@@@App interceptors token", token);
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
```

---

## 🚀 실행 방법
### ✅ 백엔드 (Spring Boot) 실행
```bash
cd backend
./mvnw spring-boot:run
```

### ✅ 프론트엔드 (Vue.js) 실행
```bash
cd frontend
npm install
npm run dev
```

## 배포 진행중에 있습니다.
https://justsaying.co.kr 접속 시 정상 동작 여부 확인중
백엔드: http:/15.165.179.197:8080
프론트엔드: http://web-2025-version1.s3-website.ap-northeast-2.amazonaws.com
---

## 🔧 추가 기능 개선 아이디어
- 네이버, 구글 소셜 로그인 추가
- 회원 프로필 사진 업로드 기능

## 🤝 기여 방법
1. 이 프로젝트를 **Fork**합니다.
2. 새로운 브랜치를 만듭니다. (`git checkout -b feature-branch`)
3. 기능을 추가하거나 수정합니다.
4. 변경 사항을 커밋합니다. (`git commit -m "설명"`)
5. 원격 저장소에 푸시합니다. (`git push origin feature-branch`)
6. **Pull Request**를 생성합니다.

---

## 📜 라이선스
이 프로젝트는 **MIT 라이선스**를 따릅니다.

🔥 일기 작성과 안전한 로그인 기능을 갖춘 웹 애플리케이션! 🙌 함께 개발하고 싶다면 언제든지 기여해주세요!

