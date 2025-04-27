package com.domain.demo_backend.util;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import scala.collection.Seq;
import scala.collection.JavaConverters;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer.KoreanToken;

import java.util.List;

public class KoreanTextExample {

    public static void main(String[] args) {
        String text = "학교에 갔어요ㅋㅋ";

        // 1. 정제 (이상한 문자, 띄어쓰기 수정)
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // 2. 형태소 분석 (단어 단위로 쪼개기)
        Seq<KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

        // 3. 결과 출력
        List<KoreanToken> tokenList = JavaConverters.seqAsJavaList(tokens);
        for (KoreanToken token : tokenList) {
            System.out.println(token.getText());
        }
    }
}

