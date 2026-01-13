import React, { useState, useEffect } from "react";
import axios from "axios";
import DynamicEngine from "../components/DynamicEngine";

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
            try{
                // AuthService의 login API 호출
                const response = axios.post("/api/auth/login", formData);
                console.log("로그인 시도!", formData); // 여기서 AuthService.login 호출
                // 로그인 성공 시 토큰 저장 및 이동
                console.log("로그인 성공!",response.data);
            } catch (error){
                // "존재하지 않는 계정입니다" 등의 에러 메시지 처리
                console.log("로그인 실패:", error.response.data.message);
            }
        } else if (item.ACTION_TYPE === "LINK") {
            window.location.href = item.ACTION_URL;
        }
    };

    return (
        <div>
            <DynamicEngine
                metadata={metadata}
                onChange={handleChange}
                onAction={handleAction}
            />
        </div>
    );
}

export default Login;