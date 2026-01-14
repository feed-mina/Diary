function ImageField ({ label, style, className }){
    // label이 "dino_diary.png"라면 경로가 "/img/dino_diary.png"가 됩니다.
    // public 폴더는 루트(/) 경로로 바로 접근 가능합니다.
    const imagePath = `/img/${label}`;
    return(
        <img
            src={imagePath}
            alt="ui-element"
            style={style}
            className={className}
            onError={(e) =>{
                console.log("이미지 로드 실패. 시도한 경로:", e.target.src);
            }}
        />
    );
}
export default ImageField;