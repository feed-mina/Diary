package com.domain.demo_backend.util;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import scala.collection.Seq;
import scala.collection.JavaConverters;

public String analyze(String text) {
    // 정제
    CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

    // 형태소 분석
    Seq<KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

    // Java에서 사용하기 쉽게 리스트 변환
    List<KoreanToken> tokenList = JavaConverters.seqAsJavaList(tokens);

    // 형태소 출력
    StringBuilder sb = new StringBuilder();
    for (KoreanToken token : tokenList) {
        sb.append(token.getText()).append(" ");
    }
    return sb.toString();
}
