package com.domain.demo_backend.diary.domain;

import com.domain.demo_backend.user.domain.User;
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
@AllArgsConstructor // 빌더를 위해 추가
@Builder // 클래스 위에 붙으면 모든 빌드에 대해 빌더가 생긴다.
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    private String title;
    private String content;

    @ManyToOne // 사용자와 연결이 있나?
    @JoinColumn(name="user_sqno")
    private User user;
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

    @PrePersist
    public void prePersist(){
        this.regDt = LocalDateTime.now();
        this.updtDt = LocalDateTime.now();
    }
}
