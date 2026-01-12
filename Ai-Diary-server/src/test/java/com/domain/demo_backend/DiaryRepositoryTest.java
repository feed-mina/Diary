package com.domain.demo_backend;

import com.domain.demo_backend.diary.domain.Diary;
import com.domain.demo_backend.diary.domain.DiaryRepository;
import com.domain.demo_backend.user.domain.User;
import com.domain.demo_backend.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DiaryRepositoryTest {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("일기 저장 및 조회 테스트")
    public void diary_create_test(){
        // 1.Given 사용자 준비 : 외래키 제약조건 때문
        User user = User.builder()
                .email("test@test.com")
                .username("피드민아")
                .build();
        userRepository.save(user);

        // 2. Given : 다이어리 객체 생성
        Diary diary = Diary.builder()
                .user(user) // 위에서 저장한 유저 객체를 넣어준다.
                .title("오늘의 일기")
                .content("JPA 테스트 중입니다")
                .author("민아")
                .delYn("N")
                .build();

        // 3. When 저장 실행
        Diary savedDiary = diaryRepository.save(diary);

        // 4. Then : 검증
        // 무엇과 무엇을 비교하면 좋을까? savedDiary의 ID가 null이 아닌지 제목이 일치한지 확인하기
        // 4. Then : 검증
// 1) DB가 생성해준 ID가 정말 존재하는지 확인
        assertThat(savedDiary.getDiaryId()).isNotNull();

// 2) 내가 저장한 제목이 가져온 데이터의 제목과 같은지 확인
        assertThat(savedDiary.getTitle()).isEqualTo("오늘의 일기");

// 3) 연결된 유저의 이메일이 맞는지 확인 (객체 그래프 탐색)
        assertThat(savedDiary.getUser().getEmail()).isEqualTo("test@test.com");

        System.out.println("테스트 성공! 생성된 다이어리 ID: " + savedDiary.getDiaryId());


    }
}
