function ButtonField({ meta, onAction }) {
    return (
        <button
            className={meta.css_class}
            style={JSON.parse(meta.inline_style || "{}")}
            onClick={() => onAction(meta)}
        >
            {meta.label_text}
        </button>
    );
}

export default ButtonField;