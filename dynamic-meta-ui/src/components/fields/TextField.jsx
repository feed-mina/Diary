function TextField({ meta, remoteData }) {
    const customStyle = JSON.parse(meta.inline_style || "{}");
    // 데이터 소스가 있고, 실제 데이터 내용이 있을 때만 데이터를 보여줌
    if (meta.ref_data_id && remoteData.data && remoteData.data.length > 0) {
        return (
            <div className={meta.cssClass} style={customStyle}>
                {/* 데이터 리스트에서 첫 번째 항목의 제목 등을 출력 */}
                {remoteData.data[0].title || "데이터가 있습니다"}
            </div>
        );
    }

    // 데이터가 없거나 로딩 중이면 DB에 적힌 원래 글자(label_text)를 보여줌
    return <div className={meta.cssClass}   style={customStyle}>{meta.labelText}</div>;
}
export default TextField;