
<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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

    const userId = localStorage.getItem('userId');

    console.log("userId : ",userId);
    // userId와 response.data.diaryList.list.userId같은지, 같다면 내가 쓴 일기만 보기 체크박스 누를때 두개가 같은 것만 response.data.diaryList 보이기
    const fetchDiaryList = async ( ) => {
      try {
        const response = await axios.get('http://localhost:8080/api/diary/viewDiaryList', {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          },
          params: {
            userId: showOnlyMine.value? userId:null,
            pageNo: page.value.pageNo,
            pageSize: page.value.pageSize,
          },
        });
        console.log("API 응답 데이터: ", response.data);
  
        console.log("diaryList : ",response.data.diaryList,response.data.diaryList.length,"개" );
        
        const { diaryList, total, pageSize, page: pageNum } = response.data;
        diaries.value = diaryList || [];
        page.value = { pageNo: pageNum, pageSize, total };
        const userIds = diaryList.map(diary => diary.userId);
        
        for( let i = 0; i < response.data.diaryList.length; i++){
          console.log("diaryList : ", diaryList);
          console.log("diaryList : ", diaryList[i].userId);
          console.log("userIds: ", userIds);
          console.log("userId : ", userIds[i]);
            if(diaryList[i].userId == userId){
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
        // console.log("showMineEqualsList: ",showMineEqualsList)


        // 내가 쓴 일기가 없을 경우 처리
        if( userIds.length === 0){
          
          console.log("showOnlyMine : ",showOnlyMine.value);
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
  
    const viewDiary = async (diaryId) => {
      cookies.set("diaryId",diaryId);
      await fetchDiaryList();
      router.push(`/diary/view/${diaryId}`);
    };

        // 컴포넌트 마운트 시 일기 목록 로드

    onMounted(() => {
      if (!userId) {
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
    <h1>일기장 리스트</h1>
  <div class="filter-section">
    <label class="filter-checkbox">
      <input type="checkbox" v-model="showOnlyMine" @change="toggleFilter"/>
    내가 쓴 일기만 보기
  </label>    
  <!-- <p>필터 상태: {{ showOnlyMine }}</p> -->
  </div>
    <div class="diaryList_content">
      <main class="diaryOtherList">
        <div class="diaryListSection" v-if="diaries.length > 0">
          <div  v-for="(it, index) in diaries" :key="it.diaryId" class="diary-post" @click="viewDiary(it.diaryId)">
            <header>
              {{ diaries.userId }}
              <h3 class="diaryAuthor">
                {{ it.author || '익명' }} 
                {{ it.userId }}
              </h3>
              <span class="diaryTitle">
                &nbsp;{{ it.title ? it.title.substring(0, 10) : '제목 없음' }}...
              </span>
              <time class="diaryTime" :dateTime="it.regDt">
                {{ new Date(it.regDt || it.date).toLocaleDateString() }}
              </time>
            </header>
            <p class="diaryContent">{{ it.content ? it.content.substring(0, 50) : '내용 없음' }}</p>
          </div>
        </div>
        <div v-else>일기가 없습니다.</div>
      </main>
      <!-- 페이지네이션 -->
      <div class="pagination" v-if="page.total > page.pageSize">
        <button
          v-for="p in Math.ceil(page.total / page.pageSize)"
          :key="p"
          :class="{ active: p === page.pageNo }"
          @click="changePage(p)"
        >
          {{ p }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

.filter-section {
  display: flex;
  justify-content: flex-end; /* 오른쪽 정렬 */
  padding: 10px;
}

.filter-checkbox {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f4f4f4;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
}

.filter-checkbox input[type='checkbox'] {
  margin-right: 10px;
}

.diaryList {
  display: flex;
	flex-wrap: wrap;
  width: 100%;
  min-width: 700px;
  margin: 0 auto;
  border-radius: 2em;
  overflow: hidden;
}

.diaryListSection{
  flex: 1 1 60%;
  display: grid;
  gap : 1rem;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
    /* auto-fit: 가능한 열로 채움, minmax로 최소 200px 이상, 최대 1fr */

}

.diaryList_content {
  width: 97%;
  background: linear-gradient(transparent, transparent 28px, #eee7db 28px);
  background-size: 30px 30px;
  display: flex;
  flex-direction: column;
}

.diaryOtherList {
  padding: 1.875em;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: flex-start;
}
.diary-post {
  background: #ffffff;
  border: solid 1px #ccc;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s ease-in-out;
}

.diary-post:hover {
  transform: scale(1.05);
}

.diary-post header h3 {
  margin-bottom: 0.5em;
  white-space: nowrap; /* 내용 길게 표시 방지 */
  overflow: hidden;
  text-overflow: ellipsis;

  text-align: left; /* 제목 중앙 정렬 */
}

.diary-post header span {
  display: block;
  margin-top: 0.5em;
  white-space: nowrap; /* 내용 길게 표시 방지 */
  overflow: hidden;
  text-overflow: ellipsis;

  text-align: left; /* 부제목 중앙 정렬 */
}

.diary-post p {
  text-align: left; /* 내용 왼쪽 정렬 */
  margin-top: auto; /* 아래로 밀기 */
  white-space: nowrap; /* 내용 길게 표시 방지 */
  overflow: hidden;
  text-overflow: ellipsis;
}
.pagination {
  margin-top: 1em;
  display: flex;
  justify-content: center;
}

.pagination button {
  margin: 0.5em;
  padding: 0.5em 1em;
  border: none;
  background-color: #c1ab86;
  color: white;
  border-radius: 5px;
  cursor: pointer;
}
.diaryTime{
  font-size: 1.5em;
}
.diaryTitle{
  font-size: 2em;
}
.diaryAuthor{
  font-size: 1.5em;
}

.diaryContent{
  font-size: 1em;
}
.pagination button.active {
  background-color: #805a3b;
}

.pagination button:hover {
  background-color: #a8835b;
}

/* 
@media screen and (min-width: 600px) {
  .diary-post {
    flex: 1 1 calc(50% - 2em);
    max-width: calc(50% - 2em);
  }
}

@media screen and (min-width: 900px) {
  .diary-post {
    flex: 1 1 calc(33.333% - 2em);
    max-width: calc(33.333% - 2em);
  }
} */

</style>
