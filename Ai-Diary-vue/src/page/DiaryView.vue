<script>

import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import axios from 'axios';
import Cookies from 'universal-cookie';

import Swal from "sweetalert2";
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
export default {
  name: 'DiaryView',
  setup() {
    const router = useRouter();
    const route = useRoute(); // 현재 라우트 정보 가져오기
    const cookies = new Cookies();
    const diaryData = ref(null);
    const diaryContentItem = ref({
      date: '', // YYYY-MM-DD 형식으로 날짜 설정
      author: '',
      title: '',
      tags: ({tag1: '', tag2: '', tag3: ''}),
      emotion: '',
      content: " ",
      hidden: true,
      emotionItems: [
        {text: "😁 기분이 좋아요", value: "1"},
        {text: "😂 너무 웃겨요", value: "2"},
        {text: "😫 어떡해야 할까요?!", value: "3"},
        {text: "😒 불쾌하고 지루해요", value: "4"},
        {text: "😤 어떻게 이럴 수가", value: "5"},
        {text: "😡 화가 나요", value: "6"},
        {text: "🤯 여기서 벗어나고 싶어요...", value: "7"},
        {text: "💖 사랑이 넘쳐요", value: "8"},
        {text: "🤕 몸 상태가 좋지 않아요", value: "9"},
        {text: "💙 우울해요", value: "10"}
      ],
    });
    const showOnlyMine = ref(false); // 내가 쓴 일기만 보기 체크박스
    // URL에서 diaryId 추출
    // const diaryId = computed(() => {
    //   return route.params.id || cookies.get("diaryId"); // URL 없으면 쿠키에서 가져옴
    // });

    const diaryId = route.params.diaryId;
    const userId = route.query.userId;
    const jwtToken = cookies.get("jwt")?.jwt; // 쿠키에서 jwt 속성 가져오기

    console.log("🛠 route.params: ", route.params);
    console.log("🛠 Extracted diaryId:", diaryId);
    console.log("🛠 Extracted userId:", userId);
    if (!diaryId) {
      console.warn("🚨 diaryId가 없음 (API 요청 중단)");
      return;
    }


    const fetchDiaryDetails = async () => {
      if (!diaryId) {
        console.warn("🚨 diaryId가 없음 (API 요청 중단)");
        return;
      }
      if (!userId) {
        console.warn("🚨 userId가 없음 (API 요청 중단)");
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`
        // , {
        //   headers: {
        //     Authorization: `Bearer ${jwtToken}`,
        //     'Content-Type': 'application/json',
        //   }
        // }
      );

        diaryData.value = response.data;
        console.log("📌 서버 응답 데이터:", diaryData.value);
      } catch (error) {
        console.error('Error fetching diary details:', error);
      }
    };


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
        const response = await axios.get(`http://localhost:8080/api/diary/viewDiaryItem/${diaryId}`
        , {
          // headers: {
          //   Authorization: `Bearer ${jwtToken}`,
          //   'Content-Type': 'application/json',
          // },
          params: {
            userId: userId, // 체크박스 상태에 따라 userId필터링
          },
        });

        Object.assign(diaryContentItem.value, response.data);
        // 응답 데이터 설정
        const objectResponse = Object.assign(diaryContentItem.value, response.data);
        console.log(objectResponse);
        // 숨겨진 일기인지 확인
        if (diaryContentItem.value.hidden && diaryContentItem.value.userId !== userId) {
          alert('접근 권한이 없습니다.');
          Swal.fire({
            title: "본인확인",
            text: '접근 권한이 없습니다.',
            icon: "warning",
            confirmButtonText: "취소",
            confirmButtonColor: "#FFA500",
            background: "#f5f5f5",
            color: "#999"
          });

        }
        return response.data;
      } catch (error) {
        console.error('Error fetching diary list: ', error);
      }

    };

    watchEffect(() => {
      if (diaryId) {
        console.log("diaryId 감지됨:", diaryId);
        getDiaryItem();
        fetchDiaryDetails();
      }
    });

    onMounted(async () => {
      const requestUrl = `http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`;
      console.log("📌 상세 페이지 요청 URL:", requestUrl);

      await fetchDiaryDetails(diaryId, userId);
    });

    const sendDiaryContentItem = async () => {

      try {
        // const { title, date, author, tags, emotion, content, hidden} = diaryContentData.value; 

        console.log("jwtToken: ", jwtToken);
        if (!jwtToken) {
          Swal.fire({
            title: "로그인 필요!",
            text: "로그인한 사람만 가능합니다.",
            icon: "warning",
            confirmButtonText: "로그인페이지이동",
            confirmButtonColor: "#FF5733",
            background: "#f5f5f5",
            color: "#999"
          }).then(() => {
            router.push("/login");
          })
          // alert("로그인한 사람만 가능합니다.");
          return;
        }

        const diaryDataToSave = {
          // pageNo: 1,
          // pageSize: 10,
          ...sendDiaryContentItem.value,
          diaryStatus: diaryContentItem.value.hidden ? 'true' : 'false', // Boolean을 문자열로 변환
          userId,
        };
        console.log('diaryDataToSave', diaryDataToSave);

        const response = await axios.post(`http://localhost:8080/api/diary/getDiaryList2/${diaryContentItem.value.diaryId}/${userId}`, diaryDataToSave
        // ,{
        //   headers: {
        //     Authorization: `Bearer ${jwtToken}`,
        //     "Content-Type": "application/json",
        //     "X-Forwarded-For": "127.0.0.1",
        //   },
        //   withCredentials: true, // 쿠키 인증 허용

        // }
      );

        console.log('response', response);
        // alert("일기장이 저장되었습니다");
        Swal.fire({
          title: "로그인 필요!",
          text: "로그인한 사람만 가능합니다.",
          icon: "success",
          confirmButtonText: "좋아요 !",
          confirmButtonColor: "#A5778F",
          background: "#f5f5f5",
          color: "#999"
        }).router.push("/diary/common").then(() => location.reload());
        return response.data;
      } catch (error) {
        console.error("API 호출 실패", error);
        // alert("일기장 저장 중 오류가 발생했습니다.");
        Swal.fire({
          title: "저장 실패!",
          text: "일기장 저장 중 오류가 발생했습니다.",
          icon: "error",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733",
          background: "#f5f5f5",
          color: "#999"
        });
        if (error.response && error.response.status === 400) {
          // alert("일기장 저장이 되지 않았습니다. 다시 시도해주세요.");
          // alert(error.response.data); 
          Swal.fire({
            title: "저장 불가",
            text: error.response.data,
            icon: "warning",
            confirmButtonText: "다시시도.",
            confirmButtonColor: "#FFA500",
            background: "#f5f5f5",
            color: "#999"
          });
          // errorMessage.value.email = error.response.data;
        } else {
          console.error("API 호출 실패", error);
          // alert("일기장 저장이 되지 않았습니다. 다시 시도해주세요.");      
          Swal.fire({
            title: "API 호출 실패",
            text: error.response.data,
            icon: "warning",
            confirmButtonText: "다시 시도",
            confirmButtonColor: "#FFA500",
            background: "#f5f5f5",
            color: "#999"
          });
        }
      }
    };

    console.log(`📌 요청 URL1:  http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`);

    console.log(`📌 요청 URL2: http://localhost:8080/api/diary/viewDiaryItem/${diaryId}?userId=${userId}`);
    return {
      diaryContent: diaryContentItem,
      showOnlyMine,
      diaryData,
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
