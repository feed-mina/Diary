# Diary — fix-guide 수정·검증 지시

한국시간 2026년 9월 18~19일 업데이트 보고서. 활동 집계 마감은 9월 19일 21:24:15입니다.

일기 작성·조회와 인증을 다루는 Vue·Spring 프로젝트입니다. 이번에는 기능별 코드·API·DB 관계와 문제 진단 절차를 담은 인수인계 묶음이 추가됐습니다.

| 항목 | 기준 |
|---|---|
| 보고서 범위 | 이번 기간의 변경과 관련 기능. 전체 시스템 설명은 아래 기존 상세 문서로 연결합니다. |
| 확인 브랜치 | main |
| 소스 기준 | `bcac74c1a1bb` |
| 검증 범위 | router의 대문자 ConfirmPassword2 import와 기준 Git 트리의 소문자 confirmPassword2.vue를 직접 대조했습니다. |
| 보고서 세트 | [쉬운 설명](easy-guide.md) · [수정·검증 지시](fix-guide.md) · [코드 인수인계](screen-code-handover.md) |

## 0. 식별과 상태

**비밀번호 확인 화면 import의 파일명 대소문자 불일치**

[2026-09-18~19 KST / Ai-Diary-vue/src/router/index.js (3,344바이트, 73줄, 파일 지문 f4f4f4878f4b) / main]

상태: **현재 Git 경로와 import 대조로 확인**. 이번 변경은 보고서 작성이며, 아래 애플리케이션 수정이나 운영 작업은 실행하지 않았습니다.

## 1. 현상

| 기대 | 확인한 실제 상태 |
|---|---|
| 라우터가 실제 존재하는 화면 파일을 불러와야 합니다. | 코드는 @/page/ConfirmPassword2.vue를 가져오지만 Git 경로는 src/page/confirmPassword2.vue입니다. |

## 2. 원인과 근거

파일 이름 첫 글자의 대소문자가 다릅니다. 대소문자를 구분하는 환경에서 모듈 해석에 실패할 수 있으며 기존 인수인계에도 Vite 스캔 실패가 기록됐습니다.

[기준 소스 열기](https://github.com/feed-mina/Diary/blob/bcac74c1a1bb44563bb1e2e55b52be9102f4008e/Ai-Diary-vue/src/router/index.js)

## 3. 수정 위치

- Ai-Diary-vue/src/router/index.js — ConfirmPassword2 import
- Ai-Diary-vue/src/page/confirmPassword2.vue — 현재 파일

## 4. 수정 또는 확인 방법

1. 현재 파일명을 유지하는 최소 수정으로 import를 맞춥니다.
2. 다른 import나 동적 참조도 같은 파일을 다른 철자로 부르는지 확인합니다.
3. 대소문자 구분 환경에서 빌드하고 해당 라우트를 엽니다.

변경 전 코드:

```javascript
import ConfirmPassword2 from "@/page/ConfirmPassword2.vue";
```

제안하는 변경 예시:

```javascript
import ConfirmPassword2 from "@/page/confirmPassword2.vue";
```

예시는 제안이며 저장소 코드에 반영된 내용이 아닙니다.

## 5. 완료 기준

- [ ] 빌드가 해당 파일을 찾지 못하는 오류 없이 진행됩니다.
- [ ] /mypage/confirmPassword2 화면이 열립니다.
- [ ] 기존 /mypage/confirmPassword 화면도 유지됩니다.

## 6. 검증 방법과 제출할 근거

Ai-Diary-vue에서 의존성 설치 후 `npm run build`를 실행합니다. 로그와 두 라우트의 화면을 기록하며, 이번 보고서에서는 수정 제안까지만 작성했습니다.

실행 결과·캡처·응답 본문 중 완료 기준에 해당하는 근거를 남깁니다. 미실행 항목은 완료로 표시하지 않습니다.
