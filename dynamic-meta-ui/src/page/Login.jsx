import React, { useState, useEffect } from "react";
import axios from "axios";
import DynamicEngine from "../components/DynamicEngine";

function Login() {
    const [metadata, setMetadata] = useState([]); // 설계도 저장
    const [formData, setFormData] = useState({}); // 입력값 상자
    const [loading, setLoading] = useState(true); // 로딩 상태

    // 1. 화면이 켜지자마자 서버에서 "로그인 설계도"를 가져옵니다.
    useEffect(() => {
        axios.get("/api/ui/LOGIN_PAGE")
            .then((response) => {
                // 2. 공통 응답구조 (ApiResponse)에서 status가 success인지 확인
                if(response.data.status === "success"){
                    console.log("로그인 설계도 로딩 성공")
                    setMetadata(response.data.data); // data 필드 안에 리스트 저장
                }
        }).catch((error) => {
            console.log("db를 받지 못함 : ", error);
        })
            .finally(() => setLoading(false));
    }, []);

    // 2. 입력값이 바뀔 때 상자에 담는 기능
    const handleChange = (id, value) => {
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    // 3. 버튼 눌렀을 때 DB의 action_type을 보고 행동하는 기능

    const handleAction = async (item) => {
        const { action_type, action_url , component_id } = item;

        if (item.action_type === "SUBMIT") {
            try{
                // AuthService의 login API 호출
                const response = await axios.post("/api/auth/login", formData);
                console.log("로그인 시도!", formData); // 여기서 AuthService.login 호출
                // 로그인 성공 시 토큰 저장 및 이동
                console.log("로그인 성공!",response.data);
            } catch (error){
                // "존재하지 않는 계정입니다" 등의 에러 메시지 처리
                console.log("로그인 실패:", error.response.data.message);
            }
        } else if (item.action_type === "LINK") {
            window.location.href = item.action_url;
        }
    };

 if (loading) return <div> 화면 정보를 읽어오는 중입니다...</div>;

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