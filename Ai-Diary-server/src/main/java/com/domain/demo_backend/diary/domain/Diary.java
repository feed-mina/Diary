package com.domain.demo_backend.diary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="diary")
@Getter
@Setter
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    @Column(name="user_sqno")
    private Long userSqno;

    private String title;
    private String content;

//    @JsonProperty("tags")
//    private Map<String, String> tags;

    private String tag1;
    private String tag2;
    private String tag3;
    private String date;
    private String userId;
    private String email;
    private String username;
    private String sbsceDt;

    @Column(name="last_updt_dt")
    private String lastUpdtDt;

    private LocalDateTime regDt;
    private String diaryStatus;

    @Column(name="frst_reg_ip")
    private String frstRegIp;

    @Column(name="frst_rgst_usps_sqno")
    private BigInteger frstRgstUspsSqno;

    private String author; // 작성자 추가
    private Integer emotion; // 감정지수 추가

    @Column(name="updt_dt")
    private LocalDateTime updtDt;

    @Column(name="diary_type")
    private String diaryType;

    @Column(name="del_yn")
    private String delYn;

    @Column(name="del_dt")
    private LocalDateTime delDt;

    @Column(name="frst_dt")
    private LocalDateTime frstRegDt;

    @Column(name="role_cd")
    private String roleCd;

    @Column(name="role_nm")
    private String roleNm;

    @Column(name="last_updt_ip")
    private String lastUpdtIp;

    @Column(name="last_updt_usps_sqno")
    private BigInteger lastUpdtUspsSqno;

    @Column(name="selected_times")
    private String selectedTimes;

    @Column(name="drug_morning")
    private String drugMorning;

    @Column(name="drug_lunch")
    private String drugLunch;

    @Column(name="drug_dinner")
    private String drugDinner;

    public Diary toDiary() {
        return Diary.builder()
                .userSqno(this.userSqno)
                .userId(this.userId)
                .email(this.email)
                .title(this.title)
                .content(this.content)
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
                .diaryType(this.diaryType)
                .selectedTimes(this.selectedTimes)
                .drugMorning(this.drugMorning)
                .drugLunch(this.drugLunch)
                .drugDinner(this.drugDinner)
                .build();
    }

    @Builder
    public Diary(Long diaryId, String title, String content,  String tag1, String tag2, String tag3, String date, String userId, String email,String username, Long userSqno, String sbsceDt, String lastUpdtDt, String roleCd, String roleNm, LocalDateTime regDt, LocalDateTime updtDt, String diaryStatus, String diaryType, String delYn, LocalDateTime delDt, LocalDateTime frstRegDt, String frstRegIp, String lastUpdtIp, BigInteger frstRgstUspsSqno, BigInteger lastUpdtUspsSqno, String author, Integer emotion, String selectedTimes, String drugMorning, String drugLunch, String drugDinner) {
        this.diaryId = diaryId;
        this.title = title;
        this.content = content;
//        this.tags = tags;
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
        this.selectedTimes = selectedTimes;
        this.drugMorning = drugMorning;
        this.drugLunch = drugLunch;
        this.drugDinner = drugDinner;
    }

    @PrePersist
    public void prePersist(){
        this.regDt = LocalDateTime.now();
        this.updtDt = LocalDateTime.now();
    }

}
