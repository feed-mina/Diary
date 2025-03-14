<template>
  <div class="diaryView">
    <div class="diaryView_content">
      <div class="diaryTuto">
        <div class="diaryView_container" v-if="diaryData">
          <div class="diaryViewTitle">📖 일기 상세 보기</div>
          <div class="diaryView_noDalle">
            <p>날짜: {{ diaryData.date }}</p>
            <p>작성자: {{ diaryData.author || '익명' }}</p>
            <p>제목: {{ diaryData.title }}</p>
            <p>내용: {{ diaryData.content }}</p>
          </div>
        </div>
        <div v-else>
          <p>일기를 불러오는 중입니다...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

export default {
  name: "DiaryView",
  setup() {
    const route = useRoute();
    const diaryData = ref(null);
    const diaryId = route.params.diaryId;
    const userId = localStorage.getItem("email") || localStorage.getItem("kakaoAccessToken");

    const fetchDiaryDetails = async () => {
      if (!diaryId || !userId) return;
      try {
        const response = await axios.get(`http://localhost:8080/api/diary/viewDiaryItem/${diaryId}`, {
          params: { userId }
        });
        diaryData.value = response.data;
      } catch (error) {
        console.error('일기 데이터를 불러오는 중 오류 발생:', error);
      }
    };

    onMounted(fetchDiaryDetails);

    return {
      diaryData
    };
  }
};
</script>
