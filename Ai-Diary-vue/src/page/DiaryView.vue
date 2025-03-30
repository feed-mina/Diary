<script>
import {onMounted, ref, watchEffect} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import axios from 'axios';
// import { apiUrl } from "@/api/index.js";
import Swal from "sweetalert2";
import "notyf/notyf.min.css";
export default {
  name: 'DiaryView',
  setup() {
    const router = useRouter();
    const route = useRoute(); // 현재 라우트 정보 가져오기
    const diaryData = ref(null);
    const showOnlyMine = ref(false); // 내가 쓴 일기만 보기 체크박스
    // URL에서 diaryId 추출
    const diaryId = route.params.diaryId;
    const userId = route.query.userId;
    // console.log("🛠 route.params: ", route.params);
    // console.log("🛠 Extracted diaryId:", diaryId);
    // console.log("🛠 Extracted userId:", userId);

    const emotionItems = [
      { text: "😁 기분이 좋아요", value: "1" },
      { text: "😂 너무 웃겨요", value: "2" },
      { text: "😫 어떡해야 할까요?!", value: "3" },
      { text: "😒 불쾌하고 지루해요", value: "4" },
      { text: "😤 어떻게 이럴 수가", value: "5" },
      { text: "😡 화가 나요", value: "6" },
      { text: "🤯 여기서 벗어나고 싶어요...", value: "7" },
      { text: "💖 사랑이 넘쳐요", value: "8" },
      { text: "🤕 몸 상태가 좋지 않아요", value: "9" },
      { text: "💙 우울해요", value: "10" }
    ];
    const getEmotionText = (emotionValue) => {
      console.log("감정 상태 값 확인:", diaryData.value.emotion);

      if (!emotionValue) return "기록 없음"; // 값이 없을 때
      const found = emotionItems.find((item) => item.value === emotionValue.toString());
      return found ? found.text : "기록 없음";
    };

    if (!diaryId) {
      console.warn("🚨 diaryId가 없음 (API 요청 중단)");
      return;
    }
    const getDiaryItem = async () => {
      if (!diaryId) {
        console.warn("🚨 diaryId가 없음 (API 요청 중단)");
        return;
      }
      if (!userId) {
        console.warn("🚨 userId가 없음 (API 요청 중단)");
        return;
      }

      try {
        console.log(`📌 요청 URL: /api/diary/viewDiaryItem/${diaryId}?userId=${userId}`);

        const response = await axios.get(`/api/diary/viewDiaryItem/${diaryId}`
            , {
              params: {
                userId: userId, // 체크박스 상태에 따라 userId필터링
              },
            });
  // diaryData 받기
        diaryData.value = response.data.diaryItem;

        console.log("📌 서버 diaryData 데이터:", diaryData.value);
      } catch (error) {
        console.error('Error fetching diary list: ', error);
      }
    };

    watchEffect(() => {
      if (diaryId) {
        console.log("diaryId 감지됨:", diaryId);
        getDiaryItem();
      }
    });

    onMounted(async () => {
      console.log("@@@@@@ onMounted");
      getDiaryItem();
    });
    return {
      diaryContent: diaryData,
      showOnlyMine,
      diaryData,
      getEmotionText
    };
  }
}
</script>
<template>
  <div class="diaryView">
    <div class="diaryView_content">
      <div class="diaryTuto">
        <div class="diaryView_container" v-if="diaryData">
          <div class="diaryViewTitle">📖 일기 상세 보기</div>
          <div class="diaryView_noDalle">
            <p>날짜: {{ diaryData.date || '날짜 미정'}}</p>
            <p>작성자: {{ diaryData.author || '익명' }}</p>
            <p>제목: {{ diaryData.title }}</p>
            <p>내용: {{ diaryData.content }}</p>
            <p>작성 날짜: {{ diaryData.date ? new Date(diaryData.date).toLocaleDateString() : '날짜 미정' }}</p>
            <p>감정 상태: {{ getEmotionText(diaryData.emotioon) }}</p>
            <p>태그: {{ [diaryData.tag1, diaryData.tag2, diaryData.tag3].filter(Boolean).join(", ") }}</p>
          </div>
        </div>
        <div v-else>
          <p>일기를 불러오는 중입니다...</p>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.diaryView {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.diaryView_content {
  max-width: 600px;
  width: 100%;
  background: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.diaryViewTitle {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.diaryView_noDalle p {
  font-size: 16px;
  color: #555;
  margin: 10px 0;
}

.diaryView_noDalle p strong {
  font-weight: bold;
  color: #333;
}

.diaryView_noDalle .content {
  padding: 15px;
  background: #f8f8f8;
  border-radius: 8px;
  margin-top: 10px;
  line-height: 1.6;
}

.diaryView_container {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>