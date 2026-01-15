// src/components/DynamicEngine.js

import InputField from "./fields/InputField";
import ButtonField from "./fields/ButtonField";
import { useState } from "react";
import ImageField from "./fields/ImageField";
import TextField from "./fields/TextField";

const componentMap = {INPUT: InputField,
    TEXT: TextField,
    PASSWORD: InputField,
    BUTTON: ButtonField,
    SNS_BUTTON: ButtonField,
    LINK_BUTTON: ButtonField,
    IMAGE: ImageField
};

function DynamicEngine({ metadata , onChange, onAction, pageData}) {
    // 부모로부터 받은 metadata를 하나씩 부품으로 변환

    const containerStyle = {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        width: "100%",
        textAlign: "center",
        gap: "10px"
    };
    return (
        <div className="engine -container" style={containerStyle}>
            {metadata.map((item) => {
            const {component_type, ref_data_id, component_id} = item;
            // 1. 컴포넌트 지도에서 부품 찾기
                const typeKey = component_type ? component_type.toUpperCase() : '';
                const Component = componentMap[typeKey];
                // 2. 이 부품이 사용할 데이터 소스 찾기 (없으면 로딩 상태)
                const remoteData = pageData[ref_data_id] || {status: "loading", data: []};

                // 3. 데이터 소스 타입은 화면에 그리지 않음
                if(component_type === "DATA_SOURCE") return null;

                // 4. 지도에 등록된 부품이 있으면 렌더링
                if(Component) {
                return (
                    <Component
                        key={item.ui_id}
                        id={component_id}
                        meta={item}
                        remoteData={remoteData}
                        onChange={onChange}
                        onAction={onAction}/>
                    );
                }
                // 등록되지 않은 타입인 경우 안내 문구
                return <div key={item.ui_id}>알 수 없는 타입:{component_type}</div>;
            })}
        </div>
    );
    const renderComponent = (item) => {
        const { component_type, ref_data_id } = item;

        // 데이터가 아예 없으면 기본 상태 객체를 제공함
        const remoteData = pageData[ref_data_id] || { status: "success", data: [] };

        if (component_type === "DATA_SOURCE") return null;
        const typeKey = item.component_type ? item.component_type.toUpperCase() : ''; // 대문자로 통일
        const Component = componentMap[typeKey];
        if (Component) {
            return (
                <Component
                    key={item.ui_id}
                    meta={item}
                    remoteData={remoteData}
                    onChange={onChange}
                    onAction={onAction}
                />
            );
        }
        return null;
    };
}

export default DynamicEngine;