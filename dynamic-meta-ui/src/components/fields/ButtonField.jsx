// src/components/fields/ButtonField.js
function ButtonField({ id, label, className, style, onClick }) {
    return (
        <button
            type="button"
            id={id}
            className={className} // DB에서 가져온 'diary-nav1' 같은 클래스가 적용됨
            style={style}         // DB에서 가져온 특수 스타일 적용
            onClick={onClick} // DynamicEngine (부모)에서 넘겨준 onClick함수 실행
        >
            {label}
        </button>
    );
}
export default ButtonField;