// src/components/DynamicEngine.js

import InputField from "./fields/InputField";
import ButtonField from "./fields/ButtonField";
import { useState } from "react";

const componentMap = {
    INPUT: InputField,
    PASSWORD: InputField, // 패스워드도 입력창 부품 공유
    BUTTON: ButtonField,
    SNS_BUTTON: ButtonField,
    LINK_BUTTON: ButtonField
};

function DynamicEngine({ metadata }) {
    const [formData, setFormData] = useState({});

    const onChange = (id, value) => {
        setFormData(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const onAction = (item) => {
        const { action_type, action_url } = item;

        // 1. 제출(SUBMIT) 시 필수 입력 체크
        if (action_type === "SUBMIT") {
            const requiredFields = metadata.filter(meta =>
                meta.IS_REQUIRED === true || meta.IS_REQUIRED === 'TRUE' || meta.IS_REQUIRED === 1
            );

            for (let field of requiredFields) {
                const value = formData[field.COMPONENT_ID];
                if (!value || value.trim() === "") {
                    alert(`${field.LABEL_TEXT} 항목은 필수 입력입니다`);
                    return; // 검사 탈락 시 여기서 중단
                }
            }

            // 검사 통과 후 실제 전송 로직
            console.log("액션 타입: ", action_type, " 서버로 보낼 데이터: ", formData);
        }

        // 2. 이동(LINK) 처리
        else if (action_type === "LINK") {
            console.log(`${action_url} 주소로 이동`);
            window.location.href = action_url;
        }
    }; // onAction 함수는 여기서 끝납니다.

    return (
        <div className="main-wrap">
            {metadata.sort((a, b) => a.SORT_ORDER - b.SORT_ORDER).map((item) => {
                const Component = componentMap[item.COMPONENT_TYPE];
                if (!Component) return null;

                const customStyle = item.INLINE_STYLE ? JSON.parse(item.INLINE_STYLE) : {};

                return (
                    <Component
                        key={item.COMPONENT_ID}
                        id={item.COMPONENT_ID}
                        label={item.LABEL_TEXT}
                        className={item.CSS_CLASS}
                        style={customStyle}
                        placeholder={item.PLACEHOLDER}
                        readOnly={item.IS_READONLY}
                        onChange={(e) => onChange(item.COMPONENT_ID, e.target.value)}
                        onClick={() => onAction(item)}
                    />
                );
            })}
        </div>
    );
}

export default DynamicEngine;