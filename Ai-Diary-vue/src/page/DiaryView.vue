<script>
import {onMounted,computed , ref, watchEffect} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import axios from 'axios';
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
      if (!emotionValue || emotionValue === "0") return "기록 없음";
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

    const formattedTags = computed(() => {
      return [diaryData.value.tag1, diaryData.value.tag2, diaryData.value.tag3]
          .filter(Boolean)
          .map(tag => `#${tag}`)
          .join(', ');
    });

    const formattedDays = computed(()=>{
      return diaryData.date ? new Date(diaryData.date).toLocaleDateString()('ko-KR', {
        year : 'numeric',
        month : 'long',
        day : 'numeric'
      }) : '날짜 미정'
    })

    return {
      diaryContent: diaryData,
      showOnlyMine,
      diaryData,
      formattedTags,
      getEmotionText,
      formattedDays
    };
  }
}
</script>
<template>
  <div class="diaryView">
    <div class="diaryView_content">
      <div class="diaryTuto">
        <div class="diaryView_container animated-fadeIn" v-if="diaryData">
          <div class="diaryViewTitle">📖 일기 상세 보기</div>
          <div class="diaryView_noDalle">
            <p>날짜: {{ diaryData.date || '날짜 미정'}}</p>
            <p>작성자: {{ diaryData.author || '익명' }}</p>
            <p>제목: {{ diaryData.title }}</p>
            <p>내용: {{ diaryData.content }}</p>
            <p>감정 상태: {{ getEmotionText(diaryData.emotion) }}</p>
            <p>
              태그:
              {{ formattedTags }}
            </p>
          </div>
        </div>
        <div v-else>
          <p>일기를 불러오는 중입니다...</p>
        </div>
      </div>
    </div>
  </div>
</template>