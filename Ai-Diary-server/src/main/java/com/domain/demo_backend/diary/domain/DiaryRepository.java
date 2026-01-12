package com.domain.demo_backend.diary.domain;

import com.domain.demo_backend.diary.dto.DiaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {


    // 1, 단순 목록 조회 (페이징 없이)
//    List<DiaryResponse> findByUserId(String userId, int pageSize, int offset);

    // 1-2. 만약 페이징을 수동으로 (pageSize, offset_ 하고 싶다면 @Query를 써야 한다.
    @Query(value = "SELECT * FROM diary WHERE user_id = :userId LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Diary> findByDiaryListCustom(@Param("userId") String userId, @Param("limit") int limit, @Param("offset") int offset);
    // 2. 특정 일기 상세 조회 (Optional 사용)
//    Collection<Object> findDiaryItemById(String userId);

    Optional<Diary> findByDiaryIdAndUserIdAndDelYn(Long diaryId, String userId, String delYn);
    // 3. 개수 세기
    int countByUserId(String userId);
}
