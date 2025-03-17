Diary & Authentication Web App
✔ 프로젝트 개요
이 프로젝트는 Vue.js와 Spring Boot를 사용하여 **일기장 기능과 사용자 인증 (일반 로그인 및 카카오 소셜 로그인)**을 제공하는 웹 애플리케이션입니다. 사용자는 일기장에 글을 작성하고 수정할 수 있으며, JWT 및 이메일 인증을 포함한 일반 로그인과 카카오 소셜 로그인을 통해 회원가입 및 로그인이 가능합니다.

🚀 주요 기능
📒 일기장 기능
CRUD 기능: 글 작성 / 수정 / 삭제 / 조회
사용자별 일기 관리 (내가 쓴 일기만 보기 , 전체 일기 보기, 로그인한 사용자가 자신의 일기 작성 및 조회 가능)
MySQL 데이터베이스를 활용한 저장
🔐 사용자 인증 (Authentication)
1️⃣ 일반 로그인 (JWT + 이메일 인증 포함)
JWT (JSON Web Token) 기반 로그인
이메일 인증 (SMTP를 활용한 인증 코드 발송)
비밀번호 암호화 (BCrypt 적용)
2️⃣ 카카오 소셜 로그인  
OAuth 2.0 기반 카카오 로그인
카카오 프로필 정보 받아오기 (닉네임, 이메일 등)
기존 회원과 연동하여 JWT 발급

🛠 기술 스택
✅ Frontend (Vue.js)
Vue 3
Vue Router (페이지 라우팅)
로컬 스토리지 기반 상태 관리 ( onMounted + watch 활용) : 로그인 유무
Axios (백엔드 API 호출, axios.interceptor 적용)
✅ Backend (Spring Boot)
Spring Boot 3
Spring Security (인증 및 보안)
MyBatis (데이터베이스 연동)
JWT (토큰 인증)
SMTP (이메일 인증)
OAuth 2.0 (카카오 로그인)
MySQL (사용자 정보 및 일기 데이터 저장)

📂 프로젝트 구조
📁 my-project
 ├── Ai-Diary-server(Spring Boot)
 │    ├── src/main/java/com/domain/demo_backend/controller (API 컨트롤러)
 │    ├── src/main/java/com/domain/demo_backend/service (비즈니스 로직)
 │    ├── src/main/java/com/domain/demo_backend/user (계정 dto/domain)
 │    ├── src/main/java/com/domain/demo_backend/diary(다이어리 dto/domain)
 │    ├── src/main/java/com/domain/demo_backend/util (유틸리티)
 │    ├── src/main/java/com/domain/demo_backend/mapper (데이터베이스 연동)
 │    ├── src/main/resources/application.properties (설정 파일)
 │    ├── src/main/resources/mappers (마이바티스)
 │
 ├── frontend (Vue.js)
 │    ├── src/components (컴포넌트)
 │    ├── src/page (페이지 설정)
 │    ├── src/router (라우터 설정)
 │    ├── src/store (상태 관리 - 로컬 스토리지 활용)
 │    ├── src/App.vue ( **axios.interceptor 적용**)
 │
 ├── README.md
  
 
🏗️ API 명세
1️⃣ 사용자 인증 API
Method
Endpoint
설명
POST
/api/auth/register
일반 회원가입 (이메일 인증 포함)
POST
/api/auth/login
일반 로그인 (JWT 발급)
GET
/api/auth/verify-email?token=xxx
이메일 인증 확인
POST
/api/kakao/login


카카오 로그인 (개발 완료)

✔ 일반 로그인 요청 예시
{
  "email": "user@domain/demo_backend.com",
  "password": "securepassword"
}

✔ 카카오 로그인 요청 예시
{
  "accessToken": "kakao_access_token"
}

✔ 응답 예시 (JWT 포함)
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}


2️⃣ 일기장 API
Method
Endpoint
설명
POST
/api/diary/addDiaryList
일기 작성
GET
/api/diary/viewDiarylist
일기 목록 조회 (다이어리ID)
GET
/api/diary/viewDiaryItem/{diaryId}
일기 상세보기

✔ 요청 예시 (일기 작성)

{
  "count": true,
  "pageNum": 1,
  "pageSize": 5,
  "startRow": 0,
  "endRow": 5,
  "total": 4,
  "pages": 1,
  "reasonable": false,
  "pageSizeZero": false,
  "diaryList": [
    {
      "diaryId": 33,
      "userSqno": 37,
      "userId": "myeliln12",
      "author": "ㅁㅁ",
      "title": "ㅠㅠ",
      "content": "일기 내용\n오늘의 감정을 기록하세요",
      "tag1": "",
      "tag2": "",
      "tag3": "",
      "username": null,
      "diaryStatus": false,
      "emotion": 2,
      "delYn": "N",
      "date": "2025-03-17T20:32:38",
      "regDt": null,
      "lastUpdtDt": null
    },
…
  }
]}



🛠 axios.interceptor 적용 (App.vue)
axios.interceptors.request.use(
  config => {
    // 일반 로그인 토큰과 카카오 로그인 토큰 중 사용 가능한 토큰 선택하기
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


✔ 실행 방법
✔ 백엔드 (Spring Boot) 실행
backend 폴더로 이동
application.yml에서 MySQL 및 이메일 SMTP 설정
프로젝트 실행
./mvnw spring-boot:run

✔ 프론트엔드 (Vue.js) 실행
frontend 폴더로 이동
패키지 설치
npm install

프로젝트 실행
npm run dev


✔ 추가 기능 개선 아이디어
네이버, 구글 소셜 로그인 추가
회원 프로필 사진 업로드 기능 

✔ 기여 방법
이 프로젝트를 Fork합니다.
새로운 브랜치를 만듭니다. (git checkout -b feature-branch)
기능을 추가하거나 수정합니다.
변경 사항을 커밋합니다. (git commit -m "설명")
원격 저장소에 푸시합니다. (git push origin feature-branch)
Pull Request를 생성합니다.

✔ 라이선스
이 프로젝트는 MIT 라이선스를 따릅니다.

🔥 일기 작성과 안전한 로그인 기능을 갖춘 웹 애플리케이션! 🙌 함께 개발하고 싶다면 언제든지 기여해주세요!

