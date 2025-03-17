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
import java.util.*;
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
    public ResponseEntity<?> viewDiaryList(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {

        System.out.println("@@@@@@ viewDiaryList 진입 ");
        Map<String, Object> response = new HashMap<>();
        CustomUserDetails currentUser = UserInfoHelper.getMemberInfo();
        System.out.println("currentUser 값: " + currentUser);
        String currentUserName = currentUser.getUsername(); // email을 받는다
        String currentUserId = currentUser.getUserId(); // 일반로그인에서는 아이디를 받는다
        System.out.println("@@@현재 로그인한 사용자 ID: " + currentUserId);
        System.out.println("@@@currentUserName 값: " + currentUserName);
        System.out.println("@@@loggedUserId 값: " + currentUserName.split("@")[0]);

        String loggedInUserID = currentUserName.split("@")[0];

        // 내가 쓴 일기만 보기 > null 이나 empty 값 체크하기
        try {
            // 🔹 "내가 쓴 일기만 보기" 체크 시 > showOnlyMine.value가 false 로 되면 userId는 "" 가 된다. 이때가 default값 - 모든 일기를 볼 수있다.
            // 내가 쓴 일기 보기 showOnlyMine.value가 true이면 , 즉, userId가 null 이 아니면 또는 "" 값이 아니면 정말 userId랑 로그인한 id가 같은지 파악, 여기서 로그인은 이메일 앞 값이여야한다.

            // vue에서 userId가 loggedInUserId(이메일 split한 id값)값이 비동기로 받아진다
            if ( userId != "") {
                if (userId.equals(loggedInUserID)) {
                    // 요청받은 userId가 현재 로그인한 사용자 ID와 일치하지 않는다.
//                  우선 로그 먼저 보기
                    // 모든 다이어리 리스트 조회
                    PageInfo<DiaryResponse> diaryList = diaryService.selectDiaryList(loggedInUserID, pageNo, pageSize);

                    if (diaryList.getList().isEmpty()) {
                        // 일치했는데 작성한 일기가 없다.
                        response.put("diaryList", new ArrayList<>()); // 빈 리스트 반환
                        response.put("message", "작성한 일기가 없습니다.");
                        response.put("diaryList", "작성한 일기가 없습니다.");
                        response.put("total", 0);
                        response.put("page", 1);
//                        response.put("pageSize", pageSize);
                        return  ResponseEntity.ok(response);
                    }
                }
            }

            // 모든 다이어리 리스트 조회
            PageInfo<DiaryResponse> diaryList = diaryService.selectDiaryList(userId, pageNo, pageSize);
            response.put("diaryList", diaryList.getList());
            response.put("total", diaryList.getTotal());
            response.put("page", diaryList.getPageNum());
            response.put("pageSize", diaryList.getPageSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 만약 로그인한 유저가 일기를 하나도 작성하지 않았을때 > 메인으로 튕기는 현상
            System.out.println("@@@일기 조회 중 오류 발생: " + e.getMessage());
            response.put("diaryList", new ArrayList<>());
            response.put("message", "일기 조회 중 오류 발생");
            response.put("total", 0);
            response.put("page", 1);
            response.put("pageSize", pageSize);
            return  ResponseEntity.ok(response);
        }
    }

    @GetMapping("/getDiaryItem/{diaryId}")
    public ResponseEntity<?> getDiaryById(@PathVariable BigInteger  diaryId, @RequestHeader(value = "X-Current-User-Id", required = false) String currentUserId,  @RequestParam(value = "userId", required = false) String userId, HttpServletRequest request) {
        System.out.println("@@@ getDiaryItem 다이어리 컨트롤러 로직 진입");
        System.out.println("@@@ getDiaryItem diaryId"+diaryId);
        System.out.println("@@@ getDiaryItem userId"+userId);
        System.out.println("@@@ getDiaryItem currentUserId"+currentUserId);
        System.out.println("@@@ getDiaryItem request"+request);
        if (userId == null) {
            return ResponseEntity.badRequest().body(null); // userId 없으면 오류 응답
        }
        // 현재 사용자의 고유번호를 가져옴
        // 🚨 본인만 볼 수 있도록 차단하는 코드 추가
        if (!userId.equals(currentUserId)) {
            System.out.println("@@@다른 사용자의 일기를 조회할 권한이 없습니다 : " + currentUserId);
            //            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("다른 사용자의 일기를 조회할 권한이 없습니다.");
        }


        DiaryRequest diaryReq = new DiaryRequest();
        // diaryId로 데이터를 검색하는 로직
        // 요청 객체에 사용자 고유 번호를 세팅
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserId(userId);

        System.out.println("@@@5--diaryReq:: " + diaryReq);
        Map<String, Object> response = new HashMap<>();

        try {
            Set<DiaryResponse> diaryItem = diaryService.viewDiaryItem(diaryReq);
//            Set<DiaryResponse> diaryItem = diaryService.findDiaryById(diaryReq);
            System.out.println("@@@6--selectDiaryList 서비스:: " + diaryItem);
            response.put("diaryItem", diaryItem);
            System.out.println("@@@7--selectDiaryListresponse:: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            System.out.println("@@@INTERNAL_SERVER_ERROR : " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/viewDiaryItem/{diaryId}")
    @ResponseBody
    public ResponseEntity<?> viewDiaryItem(@PathVariable("diaryId") BigInteger diaryId, @RequestParam(value = "userId", required = false) List<String> userIds,  @RequestHeader(value = "X-Current-User-Id", required = false) String currentUserId,  @RequestHeader(value = "Authorization", required = false) String authorizationHeader, HttpServletRequest request) {
        String userId = userIds != null && !userIds.isEmpty() ? userIds.get(0) : null;

        System.out.println("@@@@@@ viewDiaryItem diaryId"+diaryId);
        System.out.println("@@@ viewDiaryItem userId"+userId);
        System.out.println("@@@ viewDiaryItem userIds"+userIds);
        System.out.println("@@@ viewDiaryItem currentUserId"+currentUserId);
        System.out.println("@@@ viewDiaryItem request"+request);

        if (userId == null) {
            return ResponseEntity.badRequest().body("userId가 필요합니다."); // userId 없으면 오류 응답
        }

        String token = authorizationHeader.substring(7);
        Claims claims = jwtUtil.validateToken(token);
        currentUserId = claims.get("userId", String.class);
        System.out.println("@@@ viewDiaryItem authorizationHeader"+authorizationHeader);
        System.out.println("@@@ viewDiaryItem token"+token);
        System.out.println("@@@ viewDiaryItem claims"+claims);
   /*
        if (currentUserId == null && authorizationHeader == null && !authorizationHeader.startsWith("Bearer ")) {
            currentUserId = claims.get("userId", String.class);
            System.out.println("@@@ viewDiaryItem currentUserId"+currentUserId);
        }else {
            System.out.println("@@@ 로그인 정보가 없습니다");
        }
    */
        DiaryRequest diaryReq = new DiaryRequest();
        // userId랑 diaryId랑 diaryReq로 받는다
        diaryReq.setDiaryId(diaryId);
        diaryReq.setUserId(userId);

        System.out.println("@@@viewDiaryItem 다이어리  로직 진입");
        System.out.println("@@@5--diaryReq:: " + diaryReq);
        try {
            System.out.println("@@@viewDiaryItem 서비스 로직 진입");

            Set<DiaryResponse> diaryItem = diaryService.viewDiaryItem(diaryReq);
            System.out.println("@@@6--selectDiaryList 서비스:: " + diaryItem);
            Map<String, Object> response = new HashMap<>();
            response.put("diaryItem", diaryItem);
            System.out.println("@@@7--selectDiaryList diaryItem:: " + diaryItem);
            System.out.println("@@@7--selectDiaryListresponse:: " + response);
            return ResponseEntity.ok(response);
        /*
        else{
            System.out.println("findDiaryById 서비스 로직 진입");
            Set<DiaryResponse> diaryItem = diaryService.findDiaryById(diaryReq);
            System.out.println("6--findDiaryById 서비스:: " + diaryItem);
            Map<String, Object> response = new HashMap<>();
            response.put("diaryItem", diaryItem);
            System.out.println("7--findDiaryById response:: " + response);
            return ResponseEntity.ok(response);
            }
         */
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }



    @PostMapping("/addDiaryList")
    @ResponseBody
    public ResponseEntity<?> addDiaryList(
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
        System.out.println("@@@클라이언트 IP: " + ip);
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("@@@addDiaryList authorizationHeader" + authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid.");
        }

        String token = authorizationHeader.substring(7);
        System.out.println("@@@token : " + token);
        Claims claims;
        try {
            claims = jwtUtil.validateToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token.");
        }
        String userSqnoStr = claims.get("hashedPassword", String.class); // String으로 읽기
//        BigInteger currentUserSqno = new BigInteger(userSqnoStr);
//        System.out.println("currentUserSqno 값: " + currentUserSqno);

        // 현재 로그인된 사용자 ID 가져오기 er
        CustomUserDetails currentUser = UserInfoHelper.getMemberInfo();
        System.out.println("@@@currentUser 값: " + currentUser);

        String currentUserName = currentUser.getUsername(); // email을 받는다
        String currentUserId = currentUser.getUserId(); // 일반로그인에서는 아이디를 받는다
        System.out.println("@@@currentUserId : " +currentUserId);
//        System.out.println("@@@현재 로그인된 사용자: userSqno=" + currentUserSqno);
        // user_sqno 안받아진다 ?
//        String diary_userid = currentUser.getUsername().split("@")[0];

        System.out.println("@@@diaryService 들어가기" + diaryRequest);
        try {
            if (diaryRequest.getEmail() == null || !diaryRequest.getEmail().equals(currentUserName)) {
                System.out.println("@@@diaryRequest.getEmail() : " + diaryRequest.getEmail());
                System.out.println("@@@currentUserName : " +currentUserName);
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

        return ResponseEntity.ok().body(Map.of("success", true));

    }

}