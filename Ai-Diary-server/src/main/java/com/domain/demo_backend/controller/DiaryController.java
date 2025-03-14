package com.domain.demo_backend.controller;

import com.domain.demo_backend.diary.dto.DiaryRequest;
import com.domain.demo_backend.diary.dto.DiaryResponse;
import com.domain.demo_backend.helper.UserInfoHelper;
import com.domain.demo_backend.service.DiaryService;
import com.domain.demo_backend.service.KakaoService;
import com.domain.demo_backend.util.CustomUserDetails;
import com.domain.demo_backend.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {
    private final DiaryService diaryService;

    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    @Autowired
    public DiaryController(DiaryService diaryService, KakaoService kakaoService,JwtUtil jwtUtil) {
        this.diaryService = diaryService;
        this.kakaoService = kakaoService;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping("/viewDiaryList")
    public ResponseEntity<?> viewDiaryList(Model model, DiaryRequest diaryReq,
                                           @RequestParam(value = "userId", required = false) String userId,
                                           @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                           @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {


        try {
            String currentUserId = "user" + UserInfoHelper.getCurrentUserSeq();
            System.out.println("현재 로그인한 사용자 ID: " + currentUserId);

            // 요청받은 userId가 현재 로그인한 사용자 ID와 일치하는지 검증
            if (userId != null && !userId.equals(currentUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "잘못된 접근입니다.");
            }

            PageInfo<DiaryResponse> diaryList = diaryService.selectDiaryList(userId, pageNo, pageSize, diaryReq);

            Map<String, Object> response = new HashMap<>();
            response.put("diaryList", diaryList.getList());
            response.put("total", diaryList.getTotal());
            response.put("page", diaryList.getPageNum());
            response.put("pageSize", diaryList.getPageSize());

            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일기 조회 중 오류 발생");
        }
    }

    @GetMapping("/getDiaryItem/{diaryId}")
    public ResponseEntity<?> getDiaryById(@PathVariable String diaryId, @RequestParam(value = "userId", required = false) BigInteger userId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null); // userId 없으면 오류 응답
        }
        // 현재 사용자의 고유번호를 가져옴
        BigInteger currentUserId = UserInfoHelper.getMemberInfo().getUserSqno();

        DiaryRequest diaryReq = new DiaryRequest();
        // diaryId로 데이터를 검색하는 로직
        // 요청 객체에 사용자 고유 번호를 세팅
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserSqno(userId != null ? userId : currentUserId);

        System.out.println("viewDiaryItem 다이어리 컨트롤러 로직 진입");
        System.out.println("5--diaryReq:: " + diaryReq);
        try {

            // Set<DiaryResponse> diaryItem = diaryService.viewDiaryItem(diaryReq);
            Set<DiaryResponse> diaryItem = diaryService.findDiaryById(diaryId, userId);

            System.out.println("6--selectDiaryList 서비스:: " + diaryItem);

            Map<String, Object> response = new HashMap<>();
            response.put("diaryItem", diaryItem);
            System.out.println("7--selectDiaryListresponse:: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/viewDiaryItem/{diaryId}")
    @ResponseBody
    public ResponseEntity<?> viewDiaryItem(@PathVariable("diaryId") String diaryId, @RequestParam(value = "userId", required = false) BigInteger userId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null); // userId 없으면 오류 응답
        }
        // 현재 사용자의 고유번호를 가져옴
        BigInteger currentUserId = UserInfoHelper.getMemberInfo().getUserSqno();

        DiaryRequest diaryReq = new DiaryRequest();

        // 요청 객체에 사용자 고유 번호를 세팅
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserSqno(userId != null ? userId : currentUserId);

        System.out.println("viewDiaryItem 다이어리 컨트롤러 로직 진입");
        System.out.println("5--diaryReq:: " + diaryReq);
        try {
            System.out.println("viewDiaryItem 서비스 로직 진입");

            Set<DiaryResponse> diaryItem = diaryService.viewDiaryItem(diaryReq);
            System.out.println("6--selectDiaryList 서비스:: " + diaryItem);

            Map<String, Object> response = new HashMap<>();
            response.put("diaryItem", diaryItem);
            System.out.println("7--selectDiaryListresponse:: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
