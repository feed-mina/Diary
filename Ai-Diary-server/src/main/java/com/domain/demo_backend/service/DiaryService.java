package com.domain.demo_backend.service;

import com.domain.demo_backend.diary.domain.Diary;
import com.domain.demo_backend.diary.dto.DiaryRequest;
import com.domain.demo_backend.diary.dto.DiaryResponse;
import com.domain.demo_backend.mapper.DiaryMapper;
import com.domain.demo_backend.mapper.UserMapper;
import com.domain.demo_backend.util.CustomUserDetails;
import com.domain.demo_backend.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class DiaryService {
    @Autowired
    private DiaryMapper diaryMapper;
    private UserMapper userMapper;

    private final JwtUtil jwtUtil;


    public DiaryService(DiaryMapper diaryMapper, UserMapper userMapper,JwtUtil jwtUtil) {
        this.diaryMapper = diaryMapper;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public PageInfo<DiaryResponse> selectDiaryList(String userId, int pageNo, int pageSize) {
        System.out.println("@@@다이어리 서비스 selectDiaryList진입");

//        PageHelper.startPage(pageNo, pageSize);
        List<DiaryResponse> diaryResponseList;
        int offset = (pageNo - 1) * pageSize; // ✅ OFFSET 미리 계산
        try {

           // 일기 목록 가져오기
            diaryResponseList = diaryMapper.selectDiaryList(userId, pageSize, offset) ;
            System.out.println("@@@1--diaryResponseList:: " + diaryResponseList);
            // PageInfo 객체로 페이징 결과를 반환
            return new PageInfo<>(diaryResponseList);
        } catch (Exception e) {
            System.err.println("Error fetching diary list: " + e.getMessage());
            throw new RuntimeException("일기를 조회하는 도중 오류가 발생했습니다.", e);
        }

    }
     public Set<DiaryResponse> findDiaryById(DiaryRequest diaryReq) {

        System.out.println("@@@@@@findDiaryById 서비스 로직 진입 diaryReq:: " + diaryReq);

        System.out.println("@@@findDiaryItemById sql시작" + diaryReq);

//     return diaryMapper.selectDiaryItem(diaryReq)
        return diaryMapper.findDiaryItemById(diaryReq.getUserId())
                .stream() // set을 stream으로 변환
                .map(this::convertToDto) // DTO 변환 적용
                .collect(Collectors.toSet());  // 다시 Set으로 변환

    }

    private DiaryResponse convertToDto(DiaryResponse diaryResponse) {
        DiaryResponse dto = new DiaryResponse();
        dto.setDiaryId(diaryResponse.getDiaryId());
        dto.setTitle(diaryResponse.getTitle());
        dto.setContent(diaryResponse.getContent());
        dto.setRegDt(diaryResponse.getRegDt());
        return dto;
    }

    @Transactional(readOnly = true)
    public DiaryResponse viewDiaryItem(DiaryRequest diaryReq) {

        System.out.println("@@@viewDiaryItem 서비스 로직 진입 diaryReq:: " + diaryReq);
        System.out.println("@@@selectDiaryItem sql시작" + diaryReq);
        Long diaryId = diaryReq.getDiaryId().longValue();
        System.out.println("userId: " + diaryReq.getUserId());
        String userId = diaryReq.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId 값이 올바르지 않습니다.");
        }
        try {
            // 일기 조회
            DiaryResponse diary = (DiaryResponse) diaryMapper.selectDiaryItem(
                    diaryId,
                    userId, 'N'
            );

            // ❗ 조회된 데이터가 없을 경우 예외 처리 추가
            if (diary == null) {
                throw new NotFoundException("해당 일기를 찾을 수 없습니다.");
            }

            return diary;
        } catch (Exception e) {
            System.err.println("Error fetching diary item: " + e.getMessage());
            throw new RuntimeException("일기를 조회하는 도중 오류가 발생했습니다.", e);
        }

    }


    @Transactional
    public void addDiary(DiaryRequest diaryRequest, String ip, Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 로깅을 위해 추가
        System.out.println("@@@diaryRequest-다이어리서비스: " + diaryRequest);
        System.out.println("@@@diaryRequest.toDiary()-다이어리서비스: " + diaryRequest.toDiary());


        // Claims claims = jwtUtil.validateToken(token); 토큰 검증

        //  String email = claims.getSubject();
        String email = userDetails.getUsername();
        Long correctUserSqno =  userMapper.findIndexByEmail(email);
        System.out.println("@@@ userSqno: " + correctUserSqno);
        HttpServletRequest request;
//        String authorizationHeader = request.getHeader("Authorization");


        Diary diary = Diary.builder()
                .userSqno(diaryRequest.getUserSqno() != null
                        ? diaryRequest.getUserSqno()
                        : correctUserSqno)
                .title(diaryRequest.getTitle() != null ? diaryRequest.getTitle() : "Untitled")
                .author(diaryRequest.getAuthor() != null ? diaryRequest.getAuthor() : "Undefined")
                .userId(diaryRequest.getUserId() != null ? diaryRequest.getUserId() : "Undefined")
                .content(diaryRequest.getContent() != null ? diaryRequest.getContent() : "")
                .tags(Optional.ofNullable(diaryRequest.getTags()).orElse(new HashMap<>()))
                .tag1(diaryRequest.getTag1() != null ? diaryRequest.getTag1() : "")
                .tag2(diaryRequest.getTag2() != null ? diaryRequest.getTag2() : "")
                .tag3(diaryRequest.getTag3() != null ? diaryRequest.getTag3() : "")
                .emotion(diaryRequest.getEmotion() != null ? diaryRequest.getEmotion() : 0)
                .diaryStatus(diaryRequest.getDiaryStatus() != null ? diaryRequest.getDiaryStatus() : "false")
                .frstRegIp(ip != null ? ip : "127.0.0.1")
                .build();


        System.out.println("@@@Diary 객체 생성 값: " + diary);

        diaryMapper.insertDiary(diary);
    }

}