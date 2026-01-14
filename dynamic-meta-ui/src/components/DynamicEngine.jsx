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

function DynamicEngine({ metadata , onChange, onAction}) {

    return (
        <div className="main-wrap">
            {metadata.sort((a, b) => a.sortOrder - b.sortOrder).map((item) => {
                // 데이터를 받아서 그리기 직전에 대문자로 통일하는 방법
                const typeKey = item.componentType ? item.componentType.toUpperCase() : '';
                const Component = componentMap[typeKey];
                // 부품을 못 찾으면 왜 못 찾았는지 콘솔에 찍어보기 (디버깅용)
                if (!Component) {
                    console.log("부품 찾기 실패! 타입명:", item.componentType);
                    return null;
                }

                // DynamicEngine.js 내부 수정
                const customStyle = (item.inlineStyle && typeof item.inlineStyle === 'string')
                    ? JSON.parse(item.inlineStyle)
                    : (item.inlineStyle || {}); // 문자열일 때만 파싱하고, 아니면 그대로 사용하거나 빈 객체 전달

                return (
                    <Component
                        key={item.componentId}
                        id={item.componentId}
                        label={item.labelText}
                        className={item.cssClass || "default-style"}
                        style={customStyle}
                        placeholder={item.placeholder}
                        readOnly={item.isReadonly} // 수정 불가 여부
                        required={item.isRequired} // 필수 입력 여부
                        onChange={(e) => onChange(item.componentId,e.target.value)}
                        onClick={() => onAction(item)}
                    />
                    // onChange 나 onClick은 comoonPage(부모)에서 실행한다.
                );
            })}
        </div>
    );
}

export default DynamicEngine;