 // 2026.01.14
 // end to end 테스트
 // 케이스 1 : 로그인 페이지

 describe('동적 UI 시스템 로그인 테스트', () => {

     it('로그인 페이지가 설계도대로 그려지고 로그인이 성공해야 한다', () => {

         // 1. 로그인 페이지 접속
         cy.visit('http://localhost:3000/view/LOGIN_PAGE');

         // 2. 화면이 로딩될때까지 기다림
         cy.contains('설계도를 읽어오는 중...').shoud('not.exist');

         // 3. 동적으로 그려진 입력창이 존재하는지 확인 (DB의 componentId기준)
         cy.get('input#user_email').should('be.visible');
         cy.get('input#user_pw').should('be.visible');

         // 4. 아이디와 비밀번호 입력
         cy.get('input#user_email').type('test@test.com');
         cy.get('input#user_pw').type('password123');

         // 5. 로그인 버튼 클릭
         cy.contains('로그인').click();

         // 6. 백엔드 응답 후 메인 페이지로 이동했는지 확인
         cy.url().should('include', '/view/MAIN_PAGE');

         // 7. 로컬스토리지에 토큰이 잘 저장되었는지 확인
         cy.window().then((win) => {
             const token = win.localStorage.getItem('accessToken');
             expect(token).to.exit;
         });
     });

     it('필수 값을 입력하지 않으면 경고창이 떠야한다', ()=>{
         cy.visit('http://localhost:3000/view/LOGIN_PAGE');
         // 아이디만 입력하고 비밀번호는 비워둠
         cy.get('input#user_email').type('mina@test.com');
         cy.contains('로그인').click();

         // 브라우저 경고창(alert) 내용 확인 (DynamicEngine의 필수값 체크 로직)
         cy.on('window:alert', (text) =>{
             expect(text).to.contains('Password 항목은 필수 입력입니다');
         });
     });
 });