// src/components/fields/InputField.js

function InputField({ id,label, className, style, placeholder, readOnly, isRequired, onChange }) {
    // 1. label이 있을 때만 <label> 태그를 보여줍니다.
    // 2. id는 민아 님이 말씀하신 COMPONENT_ID가 들어오며, 이는 데이터 저장 키가 됩니다.
    // 3. className에는 DB에서 넘어온 CSS_CLASS 값이 적용됩니다.

    return (
        <div className="field-container" style={style}>
            {label && <label htmlFor={id}>{label}</label>}
            <input
                type={id.include('pw') ? 'password' : 'text'}
                id={id}
                name={id} // COMPONENT_ID를 name으로 사용
                placeholder={placeholder}
                readOnly={readOnly}
                required={isRequired}
                className={className} // DB의 CSS_CLASS 적용
                style={style}         // DB의 INLINE_STYLE 적용
                onChange = {onChange} // DynamicEnginge (부모)에서 넘겨준 함수 실행
            />
        </div>
    );
}

export default InputField;