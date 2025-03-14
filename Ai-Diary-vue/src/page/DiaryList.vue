<<<<<<< HEAD
<script>
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import axios from 'axios';
import Cookies from 'universal-cookie';

export default {
  name: 'DiaryList',
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const jwtToken = cookies.get('jwt')?.jwt;

    const diaryList = ref([]);
    const diaries = ref([]);
    const showOnlyMine = ref(false); // 내가 쓴 일기만 보기 체크박스


    const page = ref({
      pageNo: 1,
      pageSize: 5, // 한 페이지당 5개의 일기
      total: 0,
    });

    const loggedInUserId = localStorage.getItem('userId');

    console.log("loggedInUserId : ", loggedInUserId);
    // loggedInUserId와 response.data.diaryList.list.userId같은지, 같다면 내가 쓴 일기만 보기 체크박스 누를때 두개가 같은 것만 response.data.diaryList 보이기
    const fetchDiaryList = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/diary/viewDiaryList', {
       
          params: {
            userId: showOnlyMine.value ? loggedInUserId : null,
            pageNo: page.value.pageNo,
            pageSize: page.value.pageSize,
          },
        });
        console.log("API 응답 데이터: ", response.data);

        console.log("diaryList : ", response.data.diaryList, response.data.diaryList.length, "개");

        const {diaryList, total, pageSize, page: pageNum} = response.data;
        // diaries.value = diaryList || [];

        diaries.value = diaryList.filter(diary => {
          if (diary.diaryStatus) {
            return true;
          }
          if (showOnlyMine.value && diary.userId === loggedInUserId) {
            return true;
          }
          return false;
        })

        page.value = {pageNo: pageNum, pageSize, total};
        const userIds = diaryList.map(diary => diary.userId);

        for (let i = 0; i < response.data.diaryList.length; i++) {
          console.log("diaryList : ", diaryList);
          console.log("diaryList : ", diaryList[i].userId);
          console.log("userIds: ", userIds);
          console.log("userId : ", userIds[i]);
          if (diaryList[i].userId == loggedInUserId) {
            console.log('localStorage에 매칭되는 id', diaryList[i].userId);
            console.log("userId : ", userIds[i]);
          }
          // else {
          //   console.log("showOnlyMine : ",showOnlyMine.value);
          //   console.log("내 일기가 없음");
          //   diaries.value = [];
          //   page.value.total = 0;
          // }
        }
        // userId 배열 추출

        // const showMineEqualsList = response.data.diaryList;


        // 내가 쓴 일기가 없을 경우 처리
        if (userIds.length === 0) {

          console.log("showOnlyMine : ", showOnlyMine.value);
          console.log("내 일기가 없음");
          diaries.value = [];
          page.value.total = 0;
        }
      } catch (error) {
        console.error('Error fetching diary list: ', error);
        diaries.value = [];
        router.push('/');
      }
    };

    // 체크박스 변경 시 호출
    const toggleFilter = async () => {
      await fetchDiaryList();
    };


    const changePage = async (newPage) => {
      page.value.pageNo = newPage;
      await fetchDiaryList();
    };

    const viewDiary = async (diaryId, diaryUserId) => {
      console.log("선택한 일기의 userId:", diaryUserId);
      // userId를 동적으로 반영하여 URL 생성
      const requestUrl = `http://localhost:8080/api/diary/viewDiaryItem/${diaryId}?userId=${diaryUserId}`;

      console.log("📌 요청 URL:", requestUrl);

      // cookies.set("diaryUserId", diaryUserId);
      cookies.set("diaryId", diaryId);
      cookies.set("loggedInUserId", loggedInUserId);  // 필요하면 쿠키에도 저장 가능

      await fetchDiaryList();
      router.push(`/diary/view/${diaryId}?userId=${diaryUserId}`); // userId 포함하여 이동
    };
    // 컴포넌트 마운트 시 일기 목록 로드

    onMounted(() => {
      if (!loggedInUserId) {
        router.push('/');
      } else {
        fetchDiaryList();
      }
    });

    return {
      diaries,
      diaryList,
      page,
      fetchDiaryList,
      changePage,
      viewDiary,
      toggleFilter,
      showOnlyMine
    };
  },
};
</script>

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
