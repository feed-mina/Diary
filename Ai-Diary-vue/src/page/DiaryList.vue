<template>
  <div class="diaryList">
    <h1>📖 일기장 리스트</h1>

    <!-- 내가 쓴 일기만 보기 -->
    <div class="filter-section">
      <label class="filter-checkbox">
        <input type="checkbox" v-model="showOnlyMine" @change="toggleFilter" />
        내가 쓴 일기만 보기
      </label>
    </div>

    <!-- 일기 목록 -->
    <div class="diaryList_content">
      <main class="diaryOtherList">
        <div class="diaryListSection" v-if="filteredDiaries.length > 0">
          <div v-for="diary in filteredDiaries" :key="diary.diaryId" @click="viewDiary(diary.diaryId)">
            <div class="diary-post">
              <header>
                <h3>{{ diary.author || '익명' }}</h3>
                <span class="diaryTitle">{{ diary.title ? diary.title.substring(0, 10) + '...' : '제목 없음' }}</span>
                <time class="diaryTime">{{ new Date(diary.date).toLocaleDateString() }}</time>
              </header>
              <p class="diaryContent">{{ diary.content ? diary.content.substring(0, 50) + '...' : '내용 없음' }}</p>
            </div>
          </div>
        </div>
        <div v-else>일기가 없습니다.</div>
      </main>

      <!-- 페이지네이션 -->
      <div class="pagination" v-if="page.total > page.pageSize">
        <button v-for="p in Math.ceil(page.total / page.pageSize)" :key="p" :class="{ active: p === page.pageNo }" @click="changePage(p)">
          {{ p }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: "DiaryList",
  setup() {
    const router = useRouter();

    // ✅ 로그인한 유저 ID 가져오기 (JWT 없이 localStorage 사용)
    const loggedInUserId = ref(localStorage.getItem("email") || localStorage.getItem("kakaoAccessToken") || "");
    const showOnlyMine = ref(false); // "내가 쓴 일기만 보기" 체크박스 상태
    const diaries = ref([]); // 전체 일기 목록
    const page = ref({ pageNo: 1, pageSize: 5, total: 0 }); // 페이지 정보

    // ✅ 일기 목록 가져오기 (API 호출)
    const fetchDiaryList = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/diary/viewDiaryList', {
          headers: { "Content-Type": "application/json" },
          params: { pageNo: page.value.pageNo, pageSize: page.value.pageSize },
        });

        diaries.value = response.data.diaryList || [];
        page.value.total = response.data.total;
      } catch (error) {
        console.error("일기 목록 조회 실패:", error);
        diaries.value = [];
        page.value.total = 0;
      }
    };

    // ✅ "내가 쓴 일기만 보기" 필터링 (프론트엔드에서 처리)
    const filteredDiaries = computed(() => {
      if (showOnlyMine.value) {
        return diaries.value.filter(diary => diary.userId === loggedInUserId.value);
      }
      return diaries.value;
    });

    // ✅ 체크박스 변경 시 필터 적용
    const toggleFilter = () => {
      fetchDiaryList();
    };

    // ✅ 페이지 변경
    const changePage = async (newPage) => {
      page.value.pageNo = newPage;
      await fetchDiaryList();
    };

    // ✅ 일기 상세 페이지 이동
    const viewDiary = (diaryId) => {
      router.push(`/diary/view/${diaryId}?userId=${loggedInUserId.value}`);
    };

    // ✅ 페이지가 로드되면 일기 목록 가져오기
    onMounted(() => {
      if (!loggedInUserId.value) {
        router.push('/login'); // 로그인 필요
      } else {
        fetchDiaryList();
      }
    });

    return {
      showOnlyMine,
      diaries,
      filteredDiaries,
      page,
      toggleFilter,
      changePage,
      viewDiary,
    };
  },
};
</script>

<style scoped>
/* 필터 체크박스 스타일 */
.filter-section {
  display: flex;
  justify-content: flex-end;
  padding: 10px;
}

.filter-checkbox {
  display: flex;
  align-items: center;
  background-color: cadetblue;
  color: white;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.filter-checkbox input {
  margin-right: 10px;
}

/* 일기 목록 스타일 */
.diaryList {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.diaryListSection {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 10px;
  width: 100%;
  max-width: 800px;
}

.diary-post {
  background: white;
  border: 1px solid #ccc;
  padding: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease-in-out;
}

.diary-post:hover {
  transform: scale(1.05);
}

.diaryTitle, .diaryContent {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 페이지네이션 */
.pagination {
  margin-top: 1em;
  display: flex;
  justify-content: center;
}

.pagination button {
  margin: 0.5em;
  padding: 0.5em 1em;
  border: none;
  background-color: #00796b;
  color: white;
  border-radius: 5px;
  cursor: pointer;
}

.pagination button.active {
  background-color: #004d40;
}
</style>
