// src/components/DynamicEngine.js

import InputField from "./fields/InputField";
import ButtonField from "./fields/ButtonField";
import { useState } from "react";

const componentMap = {INPUT: InputField,
    TEXT: InputField,
    PASSWORD: InputField,
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
                // 데이터를 받아서 그리기 직전에 대문자로 통일하는 방법
                const typeKey = item.component_type ? item.component_type.toUpperCase() : '';
                const Component = componentMap[typeKey];
                // 부품을 못 찾으면 왜 못 찾았는지 콘솔에 찍어보기 (디버깅용)
                if (!Component) {
                    console.log("부품 찾기 실패! 타입명:", item.component_type);
                    return null;
                }

                const customStyle = item.INLINE_STYLE ? JSON.parse(item.INLINE_STYLE) : {};

                return (
                    <Component
                        key={item.component_id}
                        id={item.component_id}
                        label={item.label_text}
                        className={item.css_class || "default-style"}
                        style={customStyle}
                        placeholder={item.placeholder}
                        readOnly={item.is_required}
                        onChange={(e) => onChange(item.component_id,e.target.value)}
                        onClick={() => onAction(item)}
                    />
                );
            })}
        </div>
    );
}

export default DynamicEngine;