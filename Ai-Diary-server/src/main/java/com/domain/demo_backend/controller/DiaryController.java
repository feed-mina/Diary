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
    public ResponseEntity<?> getDiaryById(@PathVariable BigInteger  diaryId, @RequestHeader(value = "X-Current-User-Id", required = false) String currentUserId,  @RequestParam(value = "userId", required = false) String userId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null); // userId 없으면 오류 응답
        }
        // 현재 사용자의 고유번호를 가져옴
        // 🚨 본인만 볼 수 있도록 차단하는 코드 추가
        if (!userId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("다른 사용자의 일기를 조회할 권한이 없습니다.");
        }


        DiaryRequest diaryReq = new DiaryRequest();
        // diaryId로 데이터를 검색하는 로직
        // 요청 객체에 사용자 고유 번호를 세팅
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserId(userId);

        System.out.println("viewDiaryItem 다이어리 컨트롤러 로직 진입");
        System.out.println("5--diaryReq:: " + diaryReq);
        try {

            // Set<DiaryResponse> diaryItem = diaryService.viewDiaryItem(diaryReq);
            Set<DiaryResponse> diaryItem = diaryService.findDiaryById(diaryReq);

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
    public ResponseEntity<?> viewDiaryItem(@PathVariable("diaryId") BigInteger diaryId, @RequestParam(value = "userId", required = false) String userId,  @RequestHeader(value = "X-Current-User-Id", required = false) String currentUserId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("userId가 필요합니다."); // userId 없으면 오류 응답
        }
        //

        if (currentUserId == null) {
            return ResponseEntity.badRequest().body("로그인 정보가 없습니다.");
        }
        DiaryRequest diaryReq = new DiaryRequest();

        // 요청 객체에 사용자 고유 번호를 세팅
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserId(userId);

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



    @PostMapping("/addDiaryList")
    @ResponseBody
    public ResponseEntity<String> addDiaryList(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody DiaryRequest diaryRequest
    ) {
        // 1) 헤더에서 IP 가져오기
        String ip = request.getHeader("X-Forwarded-For");
        System.out.println("@@@addDiaryList request" + request);
        System.out.println("@@@addDiaryList request.getHeader(\"X-Forwarded-For\")" + request.getHeader("X-Forwarded-For"));
        // 2) 없으면 request.getRemoteAddr() 사용
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        System.out.println("클라이언트 IP: " + ip);
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("@@@addDiaryList authorizationHeader" + authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid.");
        }

        String token = authorizationHeader.substring(7);
        System.out.println("token : " + token);
        Claims claims;
        try {
            claims = jwtUtil.validateToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token.");
        }
        String userSqnoStr = claims.get("userSqno", String.class); // String으로 읽기
        BigInteger currentUserSqno = new BigInteger(userSqnoStr);
        System.out.println("currentUserSqno 값: " + currentUserSqno);

        // 현재 로그인된 사용자 ID 가져오기 er
        CustomUserDetails currentUser = UserInfoHelper.getMemberInfo();
        System.out.println("currentUser 값: " + currentUser);

        String currentUserName = currentUser.getUsername();
        String currentUserId = currentUser.getUserId();
        System.out.println("현재 로그인된 사용자: userSqno=" + currentUserSqno);
        System.out.println("diaryService 들어가기" + diaryRequest);
        try {
            if (diaryRequest.getUserId() == null || !diaryRequest.getUserId().equals(currentUserId)) {
                System.out.println("diaryRequest.getUserId() : " + diaryRequest.getUserId());
                throw new IllegalArgumentException("로그인한 유저만 자신의 일기를 작성할 수 있습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 요청입니다");
        } catch (Exception e) {
            System.err.println("서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생했습니다.");
        }
        diaryService.addDiary(diaryRequest, ip, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok().body(Map.of("success", true).toString());
    }

}