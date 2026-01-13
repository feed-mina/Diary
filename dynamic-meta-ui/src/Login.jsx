import React, { useState, useEffect } from "react";
import axios from "axios";
import DynamicEngine from "./components/DynamicEngine";

function Login() {
    const [metadata, setMetadata] = useState([]); // 설계도 저장
    const [formData, setFormData] = useState({}); // 입력값 상자

    // 1. 화면이 켜지자마자 서버에서 "로그인 설계도"를 가져옵니다.
    useEffect(() => {
        axios.get("/api/ui/LOGIN_PAGE").then((res) => {
            setMetadata(res.data);
        });
    }, []);

    // 2. 입력값이 바뀔 때 상자에 담는 기능
    const handleChange = (id, value) => {
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    // 3. 버튼 눌렀을 때 DB의 action_type을 보고 행동하는 기능
    const handleAction = (item) => {
        if (item.ACTION_TYPE === "SUBMIT") {
            console.log("로그인 시도!", formData); // 여기서 AuthService.login 호출
        } else if (item.ACTION_TYPE === "LINK") {
            window.location.href = item.ACTION_URL;
        }
    };

    return (
        <div>
            <h1>로그인 페이지</h1>
            <DynamicEngine
                metadata={metadata}
                onChange={handleChange}
                onAction={handleAction}
            />
        </div>
    );
}

export default Login;