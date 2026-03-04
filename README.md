# JustSaying (Diary & Authentication Web App)

> **"JWT, OAuth, 그리고 SMTP까지. 철저하게 인증과 보안, 캐싱 파이프라인을 검증한 감정 기록 플랫폼"**

![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D?logo=vuedotjs&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![OAuth 2.0](https://img.shields.io/badge/OAuth_2.0-Kakao-FFCD00?logo=kakao&logoColor=black)

## 📌 1. 프로젝트 개요

사용자의 일상 감정을 기록하고, 이를 데이터화하여 보여주는 **웹 기반 감정 다이어리 서비스**입니다. 
단순한 텍스트 기록을 넘어, **일반 로그인(JWT + SMTP 이메일) 및 OAuth 2.0 소셜 로그인(Kakao)의 완벽한 융합**, 그리고 Vue 3 클라이언트와 Spring Boot 백엔드 간의 체계화된 API 인증 파이프라인(interceptor 통신) 설계 목적을 둔 프로젝트입니다.

* **🚀 서버 상태:** AWS 인프라 비용 소진으로 현재 호스팅이 종료되었습니다. 하단의 **로컬 실행 가이드**를 참조해 주세요.

---

## 🏗 2. 아키텍처 및 핵심 플로우 (Architecture Flow)

### 🔐 하이브리드 인증 흐름 (Hybrid Auth Pipeline)
카카오 소셜 토큰과 자체 서비스 세션 간의 충돌 문제를 막기 위해 **통합 JWT 발급** 구조로 통일한 것이 핵심입니다.

```text
[ Client (Vue 3) ] ──▶ (1. 카카오/일반 로그인 요청) ──▶ [ Spring Boot Security ]
       │                                                         │
       │                                         (2. DB Email 유저 식별 매핑)
       │                                                         │
       ◀─── (3. 통합 서버 JWT(Access Token) 발급 반환) ◀── [ Token Provider ]
       │
       ▼ (4. LocalStorage & axios.interceptor 등록)
    [ 이후 모든 API 요청 시 통합 JWT로 인가(Authorization) 제어 ]
```

---

## 🔥 3. 기술 의사결정 (Tech Reasoning)

### ① 카카오 OAuth와 내부 회원(JWT) 정보 통합 매핑
* 소셜 로그인의 생태계(Kakao Access Token)의 생명주기와 서비스 자체 세션 생명주기를 분리해야 했습니다.
* 카카오에서 응답받은 유저 프로필(Email)을 서비스 내부의 `UserEntity`와 조회/병합하여, 최종 응답은 프론트엔드가 자체 서비스 인증 규격인 **단일 JWT 포맷**으로 소화할 수 있게 통합했습니다.

### ② 클라이언트 보안 로직 (axios.interceptor + Vue Router)
* 프론트엔드 라우터(Vue Router)를 세분화하여 로그인된 사용자만 일기장(`/diary`)에 접근할 수 있게 가드를 구축했습니다.
* 매 요청마다 수동으로 토큰을 넣는 낭비를 막기 위해 **Axios Request Interceptor**를 등록, 헤더에 자동으로 `Bearer JWT`가 탑재되게 설정하여 프론트엔드 보안 코드를 깔끔히 유지했습니다.

---

## 🛠 4. 기술 스택 (Tech Stack)

### Frontend (User & Admin UX)
* **Core:** Vue.js 3, Vue Router
* **State & Fetching:** LocalStorage / Composition API (watch, onMounted), Axios
* **Deployment:** AWS S3, CloudFront

### Backend (API Server)
* **Core:** Java 17, Spring Boot 3.x, Spring Security
* **Data Access & Storage:** MyBatis, MySQL
* **Auth & Features:** JWT 토큰, SMTP (이메일 인증), OAuth 2.0 (카카오)
* **Deployment:** AWS EC2, Nginx

---

## 📝 5. 주요 기능 명세 (Features)

* **다중 로그인 정책 결합:** BCrypt 암호화 기반 자체 회원가입, 메일서버(SMTP) 본인인증, 카카오 프로필 활용 가입
* **개인화된 일기 CRUD:** 각 유저(PK 매핑)가 작성한 일기만 열람 가능하도록 보안(`@AuthenticationPrincipal` 검증)
* **감정 태그 모델링:** 향후 텍스트 AI 감성분석에 쓰일 감정 태그 해시화 DB 설계

---

## 🚀 6. 로컬 실행 방법 (Getting Started)

### Backend (Spring Boot)
```bash
git clone https://github.com/feed-mina/Diary.git
cd Ai-Diary-server
./mvnw spring-boot:run
```
*(※ application.properties의 MySQL 구동 여부 및 카카오 OAuth Key 삽입 필수)*

### Frontend (Vue 3)
```bash
cd Ai-Diary-vue
npm install
npm run dev
```

---
*Developed by Min Yerin (2년 차 풀스택/백엔드 개발자)* 
*Contacts: dbdlstltm94@gmail.com*
