# Diary — SDUI 위젯 후보

목표: 원래 화면의 역할과 코드를 확인하고, 한 위젯씩 분리할 대상을 정한다. 기준: `main` / `9da247cf829e46ea033ca74b9736e77608406902`.

일기 목록·작성과 타이머/번역 소스가 있다. 개인정보 권한·응답 자료형·실제 화면 연결을 먼저 정리해야 외주 위젯으로 옮길 수 있다.

공개 범위: 공개. 선언된 라이선스 미확인: 상용 재사용 전 본인·공동 기여자·이미지 권리 확인. 후보는 구현 완료나 재배포 허가를 의미하지 않는다.

9/18·19·20 KST 커밋 수: 0 / 5 / 0. 병합·문서 커밋 포함; 기능 수 아님. 일요일은 조사 시점까지만.

|ID|위젯 후보|현재 상태|분리 작업|
|---|---|---|---|
|R09-W01|일기 목록·페이지 이동|목록 경로 있음 / 응답·권한 검증 필요|빈 목록 diaryList가 문자열로 덮이는 분기, DTO 필드와 UI 필터 필드 차이, 서버 공개범위 확인|
|R09-W02|일기 작성·감정 입력|작성 UI와 저장 경로 있음 / 운영 검증 없음|입력 폼·저장 API 분리, 민감 필드 최소화, 중복 저장 방지와 서버 오류 메시지 계약|
|R09-W03|번역·문장별 음성 읽기|함수·서버 소스 있음 / DiaryView 사용 태그 주석 처리|독립 컴포넌트로 연결을 새로 정의; 취소·언마운트 정리·비용한도·개인정보 동의|

## R09-W01 · 일기 목록·페이지 이동

내 일기 필터와 페이지 선택으로 일기 카드 목록을 표시한다.

|항목|내용|
|---|---|
|입력|showOnlyMine,pageNo,pageSize,userId|
|처리|fetchDiaryList→Controller.viewDiaryList→Service.selectDiaryList→repository 조회·DTO 변환|
|반환·화면|diaryList,total,page,pageSize 및 카드 표시|
|API|GET /api/diary/viewDiaryList|
|저장|DiaryRepository: 일기·사용자 데이터|
|부수효과|조회, 상세 라우트 이동|
|보안·분리 경계|비공개 일기는 서버에서 차단해야 함; 클라이언트 filter만으로 보안 보장 불가. 토큰 로그 제거 검토|
|공통화 계열|paged-feed|
|구현 후 통과 기준|빈 목록·타 사용자 비공개·필터·페이지 경계 검증 필요. 실제 DB 미접속|

핵심 코드:
- [const fetchDiaryList · Ai-Diary-vue/src/page/DiaryList.vue:43](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-vue/src/page/DiaryList.vue#L43)
- [public ResponseEntity<?> viewDiaryList · Ai-Diary-server/src/main/java/com/domain/demo_backend/controller/DiaryController.java:42](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-server/src/main/java/com/domain/demo_backend/controller/DiaryController.java#L42)
- [public PageInfo<DiaryResponse> selectDiaryList · Ai-Diary-server/src/main/java/com/domain/demo_backend/service/DiaryService.java:46](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-server/src/main/java/com/domain/demo_backend/service/DiaryService.java#L46)

## R09-W02 · 일기 작성·감정 입력

제목·본문·감정·태그를 입력해 개인 기록을 저장하는 후보다.

|항목|내용|
|---|---|
|입력|일기 요청,로그인 사용자,감정·태그·공개 상태|
|처리|onClickSaveDiary→addDiaryList; Controller가 Bearer 검사와 서비스 저장 연결|
|반환·화면|저장 응답 및 UI 이동/안내|
|API|POST /api/diary/addDiaryList|
|저장|DiaryService/DiaryRepository|
|부수효과|개인 일기 DB 쓰기|
|보안·분리 경계|내용·감정은 민감한 개인 기록; 토큰 로그·소유권·공개범위 정리 후 재사용|
|공통화 계열|content-editor|
|구현 후 통과 기준|저장 성공/실패, 필수값, 공개/비공개, 타 계정 저장 방지 테스트 필요|

핵심 코드:
- [const onClickSaveDiary · Ai-Diary-vue/src/page/DiaryWriting.vue:89](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-vue/src/page/DiaryWriting.vue#L89)
- [public ResponseEntity<?> addDiaryList · Ai-Diary-server/src/main/java/com/domain/demo_backend/controller/DiaryController.java:174](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-server/src/main/java/com/domain/demo_backend/controller/DiaryController.java#L174)

## R09-W03 · 번역·문장별 음성 읽기

일기 텍스트를 일본어로 바꾸고 문장별 음성과 강조를 표시하는 후보다.

|항목|내용|
|---|---|
|입력|content 객체: 날짜·작성자·제목·본문·감정·태그|
|처리|makeFullText→translateDiary; readJapanese는 문장별 TTS 후 ended 이벤트에서 다음 문장|
|반환·화면|translated_text,tts_audio_url 및 현재 문장 강조|
|API|POST /translate_only, /tts_only|
|저장|FastAPI 외부 번역/TTS 및 음성 파일 경로|
|부수효과|개인 내용 외부 전송, 음성 재생|
|보안·분리 경계|기본 자동 전송을 가져오지 말고 명시 실행·내용 미리보기; 공급자 비용/보관 정책 필요|
|공통화 계열|translation-audio|
|구현 후 통과 기준|현재 UI 활성 기능으로 세지 않음. 공급자 호출 미실행; 타임아웃·중단·다음 문장 진행 테스트 필요|

핵심 코드:
- [const translateDiary · Ai-Diary-vue/src/components/DiaryTranslator.vue:72](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-vue/src/components/DiaryTranslator.vue#L72)
- [const readJapanese · Ai-Diary-vue/src/components/DiaryTranslator.vue:104](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-vue/src/components/DiaryTranslator.vue#L104)
- [<!--            <DiaryTranslator · Ai-Diary-vue/src/page/DiaryView.vue:158](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/Ai-Diary-vue/src/page/DiaryView.vue#L158)
- [async def translate_only · pronounce-api/app/main.py:249](https://github.com/feed-mina/Diary/blob/9da247cf829e46ea033ca74b9736e77608406902/pronounce-api/app/main.py#L249)

## 예상 작업 순서

```mermaid
flowchart LR
 A[원본 화면과 코드] --> B[표시와 업무 처리 분리]
 B --> C[SDUI 계약 및 adapter 예상안]
 C --> D{범위 검토}
 D -->|확정 후| E[한 위젯 구현 및 검증]
 D -->|수정 필요| C
```

이번 조사: 정적 소스 확인. 앱 실행·운영 API·실제 고객 화면 동등성은 검증하지 않았다. 위 흐름은 향후 작업 계획이며 현재 앱 호출 흐름이 아니다.
