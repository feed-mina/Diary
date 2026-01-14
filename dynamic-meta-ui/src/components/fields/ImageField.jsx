function ImageField ({ label, style, className }){
    const imagePath = `/src/img/${label}`;
    return(
        <img
            src={imagePath}
            alt="ui-element"
            style={style}
            className={className}
            onError={(e) => console.log("이미지 로드 실패:", e.target.src)}
        />
    );
}
export default ImageField;