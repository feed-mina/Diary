import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import './styles/main.css';
import CommonPage from "./page/CommonPage";

function App() {
  return (
      <BrowserRouter>
        <Routes>
            {/*주소창에 /view 뒤에 오는 글자를 screenId라는 이름으로 읽는다*/}
          <Route path="/view/:screenId" element={<CommonPage />} />
            {/*기본주소로 오면 로그인 페이지로 보내준다*/}
          <Route path="/" element={<CommonPage screenId="LOGIN_PAGE" />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;
