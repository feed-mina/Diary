function TextField({ meta, remoteData }) {
    const customStyle = JSON.parse(meta.inline_style || "{}");

    // 1. 만약 데이터 소스(ref_data_id)를 참조하는 텍스트라면?
    if (meta.ref_data_id) {
        if (remoteData.status === "loading") return <span style={customStyle}>...</span>;

        // 데이터가 배열로 왔다면 (목록인 경우) 하나씩 나열하거나 첫 번째를 보여줌
        if (Array.isArray(remoteData.data)) {
            return (
                <div className={meta.css_class} style={customStyle}>
                    {remoteData.data.map((item, idx) => (
                        <p key={idx}>{item.title || item.label_text || JSON.stringify(item)}</p>
                    ))}
                </div>
            );
        }
        // 단일 객체 데이터라면 해당 값을 출력
        return <div className={meta.css_class} style={customStyle}>{remoteData.data}</div>;
    }

    // 2. 데이터 참조가 없는 일반 고정 텍스트라면?
    return <div className={meta.css_class} style={customStyle}>{meta.label_text}</div>;
}

export default TextField;