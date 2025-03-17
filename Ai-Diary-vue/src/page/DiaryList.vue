<script>
import {computed, onMounted, ref} from 'vue';
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
    const noDiaryMessage = ref(""); // "작성한 일기가 없습니다." 메시지


    // ✅ 로그인 상태 체크 (localStorage 활용)
    const isLoggedIn = computed(() => {
      return !!localStorage.getItem("jwtToken") || !!localStorage.getItem("kakaoAccessToken");
    });

    // ✅ 로그인한 유저 ID 확인
    const userId = ref(localStorage.getItem("userId")  || "");
    const token = ref(localStorage.getItem("jwtToken")  || "");
    const nickname = ref(localStorage.getItem("nickname")  || "");
    const email = ref(localStorage.getItem("email")  || "");
    const kakaoToken = ref(localStorage.getItem("kakaoToken")  || "");
    const page = ref({
      pageNo: 1,
      pageSize: 5, // 한 페이지당 5개의 일기
      total: 0,
    });

    const loggedInUserId = localStorage.getItem('userId');

    console.log("나만 보기: ", showOnlyMine.value);
    console.log("로그인한 사용자 ID  : ", loggedInUserId);
    // loggedInUserId와 response.data.diaryList.list.userId같은지, 같다면 내가 쓴 일기만 보기 체크박스 누를때 두개가 같은 것만 response.data.diaryList 보이기
    const fetchDiaryList = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/diary/viewDiaryList',{
          params: {
            userId: showOnlyMine.value ? loggedInUserId : "",
            pageNo: page.value.pageNo,
            pageSize: page.value.pageSize,
          },
        });

        console.log("API 응답 데이터: ", response.data);

        // `diaryList`가 없을 경우 대비하여 기본값을 빈 배열로 설정
        // const diaryList = response.data.diaryList || [];
        // const total = response.data.total || 0;
        // const pageSize = response.data.pageSize || 5;
        // const pageNum = response.data.page || 1;
        const { diaryList, total, pageSize, page: pageNum, message } = response.data;

        console.log("diaryList 개수: ", diaryList.length);

        if (message) {
          noDiaryMessage.value = message; // "작성한 일기가 없습니다." 메시지 저장
          diaries.value = [];
        } else {
          noDiaryMessage.value = "";
          // `diaryList`에서 `diaryStatus`가 true이거나, showOnlyMine 조건에 맞는 데이터만 필터링
        diaries.value = diaryList.filter(diary => {
          if (diary.diaryStatus) return true; //  모든 유저가 볼 수 있음
            return true; // diaryStatus가 true 이면 모든 유저가 볼 수 있도록 유지
            console.log("diary.diaryStatus : ",diary.diaryStatus);
          return showOnlyMine.value && diary.userId === loggedInUserId; // ✅ 내가 쓴 일기만 보기 활성화 시

          if(showOnlyMine.value && diary.userId === loggedInUserId){
            return true;  // 체크박스가 활성화된 경우, 본인의 일기만 표시

            console.log("showOnlyMine.value : ",showOnlyMine.value);
            console.log("diary.userId : ",diary.userId);
          }

          console.log("showOnlyMine.value : ",showOnlyMine.value);
          console.log("diary : ",diary);
          // diaryStatys가 false면 기본적으로 숨김 처리
          // 사용자 ID 배열 추출
          const userIds = diaryList.map(diary => diary.userId);


        });
        }
        page.value = { pageNo: pageNum, pageSize, total };
      } catch (error) {
        // if (userIds.length === 0) {
        //   console.log("내 일기가 없음");
        //   diaries.value = [];
          // page.value.total = 0;
        //   router.push('/'); }

        console.error('Error fetching diary list: ', error);
        noDiaryMessage.value = "일기를 불러오는 중 오류가 발생했습니다.";
        diaries.value = [];
        // router.push('/');
      }
    };

    // 체크박스 변경 시 호출
    const toggleFilter = async () => {
      await fetchDiaryList();
    };

// 페이지 변경 시 호출
    const changePage = async (newPage) => {
      page.value.pageNo = newPage;
      await fetchDiaryList();
    };

    // 특정 상세 일기 보기 진입점
    console.log("특정 상세 일기 보기 진입점");
    const viewDiary = async (diaryId, diaryUserId) => {
      console.log("선택한 일기의 userId:", diaryUserId);
      // userId를 동적으로 반영하여 URL 생성
      const requestUrl = `http://localhost:8080/api/diary/viewDiaryItem/${diaryId}?userId=${diaryUserId}`;

      console.log("📌 특정 상세 일기 보기 api 요청 URL:", requestUrl);

      // cookies.set("diaryUserId", diaryUserId);
      cookies.set("diaryId", diaryId);
      // cookies.set("loggedInUserId", loggedInUserId);  // 로그인한 유저  cookie담는거
      cookies.set("diaryUserId", diaryUserId);
      router.push(`/diary/view/${diaryId}?userId=${diaryUserId}`); // userId 포함하여 이동
    };
    // 컴포넌트 마운트 시 일기 목록 로드


    // ✅ 로그인되지 않은 경우 로그인 페이지로 이동
    onMounted(() => {
      if (!isLoggedIn.value) {
        Swal.fire({
          title: "로그인이 필요합니다!",
          text: "로그인 후 일기를 작성할 수 있습니다.",
          icon: "warning",
          confirmButtonText: "로그인하기",
          confirmButtonColor: "#FF5733",
        }).then(() => {
          router.push('/');
        });
      }
      else {
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
        <!-- v-if로 존재 여부 확인 -->
        <div class="diaryListSection" v-if="diaries.length > 0">
          <div v-for="diary in diaries" :key="diary.diaryId" @click="viewDiary(diary.diaryId, diary.userId)">
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
        <div v-else>{{ noDiaryMessage }}</div>
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
