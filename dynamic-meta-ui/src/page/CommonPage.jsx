import React, {useEffect, useState} from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import DynamicEngine from "../components/DynamicEngine";
function CommonPage() {
    const { screenId } = useParams();
    const [metadata, setMetadata] = useState([]);
    const [formData, setFormData] = useState({});
    const [loading, setLoading] = useState(true); // 로딩 상태

    useEffect(() => {
        setLoading(true);
        // 주소창이 바뀔때마다 해당 ID의 설계도를 요청한다
        axios.get(`/api/ui/${screenId}`)
        .then(response => {
            console.log("response.data 서버 응답 데이터:", response.data);
            console.log("response.data.data 서버 응답 데이터:", response.data.data);
            if(response.data.status === "success"){
                setMetadata(response.data.data);
            }
            // * 데이터를 다 받았으니 로딩을 끈다.
            setLoading(false);
        })
            .catch(error => {
                console.log("에러 발생: ", error);
                setLoading(false);
            })
    },[screenId]) // screenId가 바뀔때마다 다시 실행된다

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
                localStorage.setItem("accessToken", response.data.data.accessToken);

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
                metadata={metadata}
                onChange={handleChange}
                onAction={handleAction}
            />
        </div>
    );
}
export default CommonPage;