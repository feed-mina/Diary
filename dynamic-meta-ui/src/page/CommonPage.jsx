import React, {useEffect, useState} from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import DynamicEngine from "../components/DynamicEngine";
function CommonPage() {
    const { screenId } = useParams();
    const [metadata, setMetadata] = useState([]); // 화면 설계떠
    const [formData, setFormData] = useState({});

    const [diaryList, setDiaryList] = useState([]); // 실제 일기 데이터
    const [loading, setLoading] = useState(true); // 로딩 상태

    const isLoggedIn = !!localStorage.getItem("accessToken");
    // 메타데이터 필터링
    const filtedMetadata = metadata.filter(item => {
        if (item.componentId === "login_btn"){
            return !isLoggedIn;
        }
        if(item.componentId === "go_diary_btn"){
            return isLoggedIn;
        }
        return true;
    });

    useEffect(() => {
        const initializePage = async () => {
            setLoading(true);
            try{
            //     1, 화면 설계도 (Metadata)가져오기
                const uiRes = await axios.get(`/api/ui/${screenId}`);
                if (uiRes.data.status === "success") {
                    setMetadata(uiRes.data.data);
                }
                // 2. 만약 메인 페이지라면 일기 목록도 함께 가져오기
                if (screenId === "MAIN_PAGE" && isLoggedIn) {
                    const diaryRes = await axios.get("/api/diary/viewDiaryList", {
                        headers: {
                            Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                        }
                    });
                    // 백엔드 응답 구조에 따라 diaryList추출
                    setDiaryList(diaryRes.data.diaryList ||[]);
                }
            } catch (error){
                console.log("에러 발생: ", error);
            } finally {
                setLoading(false);
            }
        };
        initializePage();
    },[screenId, isLoggedIn]) // screenId가 바뀔때마다 다시 실행된다

    const handleChange = (id, value) => {
        console.log("========handleChange============");
        setFormData(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleAction = async (item) => {
        const { actionType, actionUrl , componentId } = item;
        console.log("========handleAction============");
        console.log("실제 actionType:", actionType);
        console.log("item: ", item);


        // 1. 제출(SUBMIT) 시 필수 입력 체크
        if (actionType === "SUBMIT") {
            const requiredFields = metadata.filter(meta => meta.isRequired);
            // 2. 실제 서버로 데이터 보내기

            for (let field of requiredFields) {
                const value = formData[field.componentId];
                if (!value || value.trim() === "") {
                    alert(`${field.labelText} 항목은 필수 입력입니다`);
                    return; // 검사 탈락 시 여기서 중단
                }
            }
            // 2. 체크 통과 시 서버 전송
            try{
                console.log("서버 주소로 데이터 보내는 중:", actionUrl);

                const response = await axios.post(actionUrl, formData);
                console.log("서버 응답:", response.data);

                // 3. JWT 토큰 저장
                if (response.data.accessToken) {
                    localStorage.setItem("accessToken", response.data.accessToken);
                    localStorage.setItem("refreshToken", response.data.refreshToken); // 리프레시 토큰도 함께 저장

                    console.log("토큰 저장 완료! 메인 페이지로 이동합니다.");
                    window.location.href = `/view/MAIN_PAGE`;
                }

                window.location.href = `/view/MAIN_PAGE`;
            } catch(error){
                const errorMsg = error.response?.data?.message || "로그인 정보를 다시 확인해주세요.";
                console.log("에러 발생: ", error.message);
                console.log("errorMsg: ", errorMsg);
            }

            // 검사 통과 후 실제 전송 로직
            console.log("액션 타입: ", actionType, " 서버로 보낼 데이터: ", formData);
        }

        // 2. 이동(LINK) 처리
        else if (actionType === "LINK") {
            console.log(`${actionUrl} 주소로 이동`);
            window.location.href = item.actionUrl;
        }
    }; // onAction 함수는 여기서 끝납니다.

    if (loading) return <div> 화면 정보를 읽어오는 중입니다...</div>;

    return (
        <div className={`page-wrap ${screenId}`}>
            <DynamicEngine
                metadata={filtedMetadata}
                onChange={handleChange}
                onAction={handleAction}
            />
        </div>
    );
}
export default CommonPage;