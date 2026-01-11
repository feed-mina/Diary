<script>
import {computed, onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import Cookies from 'universal-cookie';
import Swal from "sweetalert2";
import axiosInstance from '@/unit/axiosInstance.js';
export default {
  name: 'DiaryList',
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const jwtToken = cookies.get('jwt')?.jwt;

    console.log('jwtTokencookie',jwtToken);

    const diaryList = ref([]);
    const diaries = ref([]);
    const showOnlyMine = ref(false); // 내가 쓴 일기만 보기 체크박스
    const noDiaryMessage = ref(""); // "작성한 일기가 없습니다." 메시지

    //  로그인 상태 체크 (localStorage 활용)
    const isLoggedIn = computed(() => {
      return !!localStorage.getItem("jwtToken") || !!localStorage.getItem("kakaoAccessToken");
    });

    //  로그인한 유저 ID 확인
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
        const response = await axiosInstance.get(`/api/diary/viewDiaryList`, {
          params: {
            userId: showOnlyMine.value ? loggedInUserId : "",
            pageNo: page.value.pageNo,
            pageSize: page.value.pageSize,
          },
        });

        console.log("API 응답 데이터: ", response.data);

        console.log(response.data.total);
        if (response.data.total === 0) {
          noDiaryMessage.value = "일기를 불러오는 중 오류가 발생했습니다.";
        }
        const {diaryList, total, pageSize, page: pageNum, message} = response.data;

        console.log("diaryList 개수: ", diaryList.length);

        if (message) {
          noDiaryMessage.value = message; // "작성한 일기가 없습니다." 메시지 저장
          diaries.value = [];
        }
        // else {
        //   noDiaryMessage.value = "";
          // `diaryList`에서 `diaryStatus`가 true이거나, showOnlyMine 조건에 맞는 데이터만 필터링
          diaries.value = diaryList.filter(diary => {
        console.log("@@@@@@@ filter")
            console.log("@@@@@@@ filter" + diary.diaryStatus)
            const isOwner = diary.userId === loggedInUserId;
            // 1. 비공개(diaryStatus === "true") + 작성자 본인만 보기 가능
            if (diary.diaryStatus === "true") {
              return isOwner;
            }

            // 2. 공개(diaryStatus === "false")는 누구나 볼 수 있음
            return true;
            console.log("diary.diaryStatus : ", diary.diaryStatus);
            if (showOnlyMine.value && diary.userId === loggedInUserId) {
              return true;  // 체크박스가 활성화된 경우, 본인의 일기만 표시
              console.log("showOnlyMine.value : ", showOnlyMine.value);
              console.log("diary.userId : ", diary.userId);
            }

            console.log("showOnlyMine.value : ", showOnlyMine.value);
            console.log("diary : ", diary);
            // diaryStatys가 false면 기본적으로 숨김 처리
            // 사용자 ID 배열 추출
            const userIds = diaryList.map(diary => diary.userId);
          });
        // }
        page.value = {pageNo: pageNum, pageSize, total};
      } catch (error) {
        console.error('Error fetching diary list: ', error);
        noDiaryMessage.value = "일기를 불러오는 중 오류가 발생했습니다.";
        diaries.value = [];
        router.push("/").then(() => {
          location.reload(); // 새로고침
        });
      }
    };

    // 체크박스 변경 시 호출
    const toggleFilter = async () => {
      page.value.pageNo = 1;
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
      cookies.set("diaryId", diaryId);
      cookies.set("diaryUserId", diaryUserId);
      console.log("diaryId", diaryId);
      console.log("diaryUserId", diaryUserId);
      router.push(`/diary/view/${diaryId}?userId=${diaryUserId}`); // userId 포함하여 이동
    };
    // 컴포넌트 마운트 시 일기 목록 로드

    //  로그인되지 않은 경우 로그인 페이지로 이동
    onMounted(() => {
      if (!isLoggedIn.value) {
        Swal.fire({
          title: "로그인이 필요합니다!",
          text: "로그인 후 일기를 작성할 수 있습니다.",
          icon: "warning",
          confirmButtonText: "로그인하기",
          confirmButtonColor: "#FF5733",
        }).then(() => {
          router.push("/").then(() => {
            location.reload(); // 새로고침
          });
        });
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
      showOnlyMine,
      noDiaryMessage,
      userId
    };
  },
};
</script>
<template>
  <div class="diaryList">
    <h1>📓 일기장 리스트</h1>
    <div class="filter-section">
      <label class="filter-checkbox">
        <input type="checkbox" v-model="showOnlyMine" @change="toggleFilter"/>
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

                <img
                    v-if="diary.diaryStatus === 'true' && diary.userId === userId"
                    src="/img/carroticon.png"
                    alt="비공개"
                    style="width: 20px; height: 20px; margin-left: 5px;"
                />

                <time class="diaryTime">{{ new Date(diary.date).toLocaleDateString() }}</time>
              </header>
<!--              <p class="diaryContent">{{ diary.content ? diary.content.substring(0, 50) + '...' : '내용 없음' }}</p>-->
            </div>
          </div>
        </div>
        <div v-else>
          <p class="no-diary-message">{{ noDiaryMessage }}</p>
        </div>
      </main>
      <!-- 페이지네이션 -->
<!--      <div class="pagination" v-if="page.total > page.pageSize">-->
      <div class="pagination" v-if="page.total > 0">

      <button v-for="p in Math.ceil(page.total / page.pageSize)" :key="p" :class="{ active: p === page.pageNo }"
                @click="changePage(p)">
          {{ p }}
        </button>
      </div>
    </div>
  </div>
</template>