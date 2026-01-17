function ImageField({ meta }) {
    const customStyle = JSON.parse(meta.inlineStyle || "{}");

    // label_text에 들어있는 파일명(dino_diary.png)을 사용
    // 경로가 /img/ 안에 있다면 아래처럼 합쳐줍니다.
    const fileName = meta.label_text || meta.labelText || meta.label_text.split(".")[0];

    const imagePath = fileName ? `/img/${meta.fileName}` : "/img/default.png";
    return (
        <div style={customStyle}>
            <img
                src={imagePath}
                className={meta.cssClass}
                alt="ui-element"
                style={{ width: "100%" , height: "auto" }}
                onError={(e) => { e.target.src = "/img/default.png"; }} // 에러 시 기본 이미지
            />
        </div>
    );
}
export default ImageField;