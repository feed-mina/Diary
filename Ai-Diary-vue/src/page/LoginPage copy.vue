<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>로그인 화면</title>
  <style>
    * {
      box-sizing: border-box;
    }
    body {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
      background-color: #f5f5f5;
    }
    .login-box {
      background-color: #fff;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      width: 300px;
    }
    .login-box h2 {
      text-align: center;
      margin-bottom: 20px;
    }
    .login-box label {
      display: block;
      margin: 10px 0 5px;
    }
    .login-box input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
    }
    .login-box .password-show {
      margin: 5px 0 15px;
      font-size: 12px;
      color: #555;
      cursor: pointer;
    }
    .login-btn {
      width: 100%;
      padding: 12px;
      background-color: #4285f4;
      color: #fff;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      margin-top: 10px;
      cursor: pointer;
    }
    .kakao-btn {
      width: 100%;
      padding: 12px;
      background-color: #fee500;
      color: #000;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      margin-top: 10px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .kakao-btn span {
      margin-left: 8px;
    }
  </style>
</head>
<body>

  <div class="login-box">
    <h2>로그인</h2>
    <label for="id">ID</label>
    <input type="text" id="id" placeholder="아이디를 입력하세요">

    <label for="password">Password</label>
    <input type="password" id="password" placeholder="비밀번호를 입력하세요">
    <div class="password-show" onclick="togglePassword()">👁️ 비밀번호 보기</div>

    <button class="login-btn">로그인</button>
    <button class="kakao-btn">
      🗨️ <span>카카오 로그인</span>
    </button>
  </div>

  <script>
    function togglePassword() {
      const passwordInput = document.getElementById('password');
      if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
      } else {
        passwordInput.type = 'password';
      }
    }
  </script>

</body>
</html>
