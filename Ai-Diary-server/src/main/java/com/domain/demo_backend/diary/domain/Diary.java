package com.domain.demo_backend.diary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Getter
public class Diary {

    private Long diaryId;
    private Long userSqno;

    private String title;
    private String content;
    @JsonProperty("tags")
    private Map<String, String> tags;
    private String tag1;
    private String tag2;
    private String tag3;
    private String date;
    private String userId;
    private String email;
    private String username;
    private String sbsceDt;
    private String lastUpdtDt;
    private LocalDateTime regDt;
    private String diaryStatus;
    private String frstRegIp;
    private BigInteger frstRgstUspsSqno;
    private String author; // 작성자 추가
    private Integer emotion; // 감정지수 추가

    private LocalDateTime updtDt;
    private String diaryType;
    private String delYn;
    private LocalDateTime delDt;
    private LocalDateTime frstRegDt;
    private String roleCd;
    private String roleNm;
    private String lastUpdtIp;
    private BigInteger lastUpdtUspsSqno;

    public Diary toDiary() {
        return Diary.builder()
                .userSqno(this.userSqno)
                .userId(this.userId)
                .email(this.email)
                .title(this.title)
                .content(this.content)
                .tags(this.tags)
                .tag1(this.tag1)
                .tag2(this.tag2)
                .tag3(this.tag3)
                .diaryStatus(this.diaryStatus)
                .delYn(this.delYn)
                .frstRegIp(this.frstRegIp)
                .frstRgstUspsSqno(this.frstRgstUspsSqno)
                .regDt(this.regDt)
                .author(this.author)
                .emotion(this.emotion)
                .build();
    }

    @Builder
    public Diary(Long diaryId, String title, String content, Map tags, String tag1, String tag2, String tag3, String date, String userId, String email,String username, Long userSqno, String sbsceDt, String lastUpdtDt, String roleCd, String roleNm, LocalDateTime regDt, LocalDateTime updtDt, String diaryStatus, String diaryType, String delYn, LocalDateTime delDt, LocalDateTime frstRegDt, String frstRegIp, String lastUpdtIp, BigInteger frstRgstUspsSqno, BigInteger lastUpdtUspsSqno, String author, Integer emotion) {
        this.diaryId = diaryId;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.date = date;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.userSqno = userSqno;
        this.sbsceDt = sbsceDt;
        this.lastUpdtDt = lastUpdtDt;
        this.diaryStatus = diaryStatus;
        this.delYn = delYn;
        this.frstRgstUspsSqno = frstRgstUspsSqno;
        this.frstRegIp = frstRegIp;
        this.regDt = regDt;
        this.author = author;
        this.emotion = emotion;
    }

}
