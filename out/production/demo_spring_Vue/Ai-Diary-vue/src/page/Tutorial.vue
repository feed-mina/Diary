<script>
/* eslint-disable vue/multi-word-component-names */
import {computed, ref} from 'vue';
import NotFound from '@/page/NotFound.vue';
import Swal from 'sweetalert2';

export default {
  name: 'Tutorial',
  setup() {
    const isHidden = ref(true);   // 기본값: 숨기기 상태
    const currentView = computed(() => {
      const routes = {
        '/notFound': NotFound,
      };
      const currentPath = window.location.hash.slice(1) || '/';
      return routes[currentPath] || NotFound;
    });

    const diaryContent = ref({
      date: new Date().toISOString().slice(0, 10), // YYYY-MM-DD 형식으로 날짜 설정
      author: "작성자",
      title: "제목",
      tags: ({tag1: "#tag1", tag2: "#tag2", tag3: "#tag3"}),
      emotion: "1",
      content: "오늘은 기분이 어땠나요? ",
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

    const toggleHidden = (value) => {
      isHidden.value = value;
    };
    const saveDiary = () => {
      event.preventDefault(); // 기본 동작 방지
      console.log("Diary saved");
      // alert("일기가 저장되었습니다.");
      Swal.fire({
        title: "튜토리얼모드: 저장 완료 !",
        text: "일기장이 성공적으로 저장되었습니다.",
        icon: "success",
        confirmButtonText: "좋아요!",
        confirmButtonColor: "#5DBB63",
        background: "#f5f5b5", // 배경색 변경
        color: "#330", // 글자색 변경
      }).then(() => {
        location.reload();
      })
    };

    return {
      currentView,
      diaryContent,
      toggleHidden,
      saveDiary,
      isHidden
    };
  }
}
</script>

<template>
  <div class="tutorial">
    <div class="tutorial_content">
      <div class="diaryTuto">
        <div class="tutorial_container">
          <form>
            <div class="tutorialTitle">
              <div v-tooltip="'튜토리얼 페이지입니다. 메뉴에 마우스를 올려보세요.'">
                <!-- <span class="tutorialHighlight">
                </span> -->
              </div>
            </div>
            <div class="tutorial_noDalle">
              <div class="section0">
                <div>오늘 날짜 : {{ diaryContent.date }}</div>
              </div>
              <div class="section01">
                <div>
                  <div v-tooltip="' ID가 자동 입력됩니다.'">
                    <label for="author">작성자&nbsp;&nbsp;&nbsp;</label>
                    <input type="text" class="author" id="author" name="author" :value="diaryContent.author"
                           placeholder="" readonly :disabled="false"/>
                  </div>
                  <div v-tooltip="' 제목을 입력해주세요.'">
                    <div class="titleSc">
                      <label for="title">제목&nbsp;&nbsp;&nbsp;</label>
                      <input type="text" class="title" id="title" name="title" :value="diaryContent.title"
                             placeholder="" readonly :disabled="false"/>
                    </div>
                  </div>
                </div>
              </div>
              <!--section2-->
              <div class="section2">
                <div class="text">
                  <div v-tooltip="'일기의 핵심 키워드 3개를 각각 적어주세요.'">
                    <span>오늘의 감정을 태그로 입력하세요.</span>
                  </div>
                </div>
                <div class="tags">
                  <div v-tooltip="'오늘의 감정을 태그로 입력하세요.'">
                    <input type="text" id="tag1" name="tag1" v-model="diaryContent.tags.tag1" placeholder="" disabled/>

                    <input type="text" id="tag2" name="tag2" v-model="diaryContent.tags.tag2" placeholder="" disabled/>

                    <input type="text" id="tag3" name="tag3" v-model="diaryContent.tags.tag3" placeholder="" disabled/>
                  </div>
                  <!-- <button type="button" class="aiButton">
                     <div  v-tooltip="'사진이 생성되는 동안 일기를 작성할 수  없습니다.'">
                      <span>감정 다이어리</span>
                     </div>
                  </button> -->
                </div>
              </div>
              <!--section3-->
              <div class="section3">
                <div class="text">
                  <div v-tooltip="'오늘의 내 기분을 나타내는 감정을 선택해주세요.'">
                    <span>감정지수를 선택하세요.</span>
                  </div>
                </div>
                <div class="selectBox">
                  <v-select
                      v-model="diaryContent.emotion"
                      :items="diaryContent.emotionItems"
                      item-title="text"
                      item-value="value"
                      label="오늘의 감정지수는?"
                      class="emotion"
                      id="emotion"
                      required
                  ></v-select>
                </div>
              </div>
              <!--section4-->
              <div class="section4">
                <div class="text">
                  <div v-tooltip="' 아래에서 일기의 본문을 작성해주세요.'">
                    <span>일기장</span>
                  </div>
                </div>
                <textarea v-model="diaryContent.content" rows="3" class="content" name="content" id="content"
                          disabled></textarea>
              </div>
              <!--section05-->
              <div class="section05">
                <div v-tooltip="'일기를 다른 사람에게 공유할지를 선택해주세요.'">
                  <span>🔎</span>
                  <div class="button-group">
                    <button
                        :class="{ active: isHidden }"
                        @click.prevent="toggleHidden(true)"
                    >숨기기
                    </button>
                    <button
                        :class="{ active: !isHidden }"
                        @click.prevent="toggleHidden(false)"
                    >보여주기
                    </button>
                  </div>
                </div>
              </div>
              <div class="saveDiary">
                <button type="button" @click.prevent="saveDiary">기록하기</button>
              </div>
              <!--diaryTuto-dalle-->
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>


.titleSc {
  padding-top: 2rem;
}

.tutorial {
  display: flex;
  justify-content: center;
  /* height: 100vh;  화면 전체 높이를 채움 */
  background-color: #f8f9fa; /* 배경색 */
  padding: 10px;
}

.tutorial_content {
  height: 100%;
  width: 100%; /* 모바일에서는 전체 너비 사용 */
  padding: 1rem;
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  align-items: center; /* 수평 중앙 정렬 */
}

.diaryTuto {
  padding-top: 1.5em;
  width: 90%; /* 화면 크기 따라 자동 조정 */
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  justify-content: center;

  /* overflow-y: auto; 스크롤 가능 */
  /* z-index: 9999; */
  font-size: 1em;
}

.diaryTuto input,
textarea {
  border-radius: 0.3125em;
  background: #eee7db;
  border: 0 solid black;
  font-size: 1em;
}

.tutorial_container {
  /* border: 1px solid #00fa9a; */
  margin: 0;
  width: 100%;
  display: flex;
  flex-direction: row;
}

.tutorialTitle {
  font-size: 2rem;
  text-align: center;
  font-weight: bold;

}

.tutorialTitle {
  box-shadow: inset 0 -23px 0 #e89b3d;
  display: inline;
}

.tutorial_container form {
  width: 100%;
  height: 100%;
}

.tutorial_noDalle {
  width: 65%;
  height: 100%;
  margin: 0em;
  float: left;
}

/* .tutorial_noDalle input,
select,
button,
option {
  height: 1.875em;
} */

.tutorial_container input:focus {
  outline: 2px solid #c1ab86;
  transition: 0.1s;
}


.tutorial_noDalle .section0 {
  width: 100%;
  display: flex;
  flex-direction: row;
  margin-bottom: 0.9375em;
}

.tutorial_noDalle .section01 {
  margin-top: 0.125em;
  width: 100%;
  display: flex;
  flex-direction: row;
  margin-bottom: 0.9375em;
}

.tutorial_noDalle .section01 input {
  width: 40%;
  text-align: center;
}

.tutorial_noDalle .section1 input:hover {
  transition: 0.3s;
  opacity: 1;
  outline: 2px solid #c1ab86;
  width: 42%;
}

.tutorial_noDalle .section2 {
  width: 100%;
  margin-bottom: 0.625em;
}

.tutorial_noDalle .section2 .tags {
  padding-top: 0.625em;
  width: 95%;
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
}

.tutorial_noDalle .section2 input {
  text-align: center;
  width: 25%;
  margin: 0.3125em;
  caret-color: #604e2e;
}

.tutorial_noDalle .section2 input:hover {
  transition: 0.3s;
  opacity: 1;
  outline: 2px solid #c1ab86;
  width: 27%;
}

/* .tutorial_noDalle .section2 button {
   width: 25%;
   margin: 0.3125em;
   height: 2em;
   background: #c1ab86;
 } */
.tutorial_noDalle .section2 button:hover {
  transition: 0.3s;
  opacity: 1;
  background: #604e2e;
  color: white;
  width: 30%;
}

.tutorial_noDalle .section3 {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.25em;
}

.tutorial_noDalle .section3 .text {
  margin-bottom: 0.625em;
}

.tutorial_noDalle .section3 select {
  margin-left: 0.625em;
  border-radius: 0.25em;
  border: 1px solid #c1ab86;
  outline: 0 none;
  text-align: center;
  width: 35%;
  height: 2.5em;
}

.tutorial_noDalle .section3 .selectBox .v-select {
  background: transparent;
  outline: 0 none;
  padding: 0 5px;
  position: relative;
}

.tutorial_noDalle .section3 .selectBox .v-select option {
  color: #c1ab86;
  padding: 0.1875em 0;
  font-size: 1em;
  border-radius: 0.25em;
  text-align: center;
}

.tutorial_noDalle .section3 .selectBox .icoArrow {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 1;
  width: 2.1875em;
  height: inherit;
  display: flex;
  justify-content: center;
  align-items: center;
}

.tutorial_noDalle .section3 .selectBox .icoArrow img {
  width: 50%;
  transition: 0.3s;
}

.tutorial_noDalle .section3 .selectBox .v-select:focus + .icoArrow img {
  transform: rotate(180deg);
}

.tutorial_noDalle .section4 {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.25em;
  margin-left: 0.3125em;
  width: 95%;
}

.tutorial_noDalle .section4 .text {
  margin-bottom: 0.625em;
}

.tutorial_noDalle .section4 textarea {
  width: 100%;
  padding: 0.625em;
  font-size: 1em;
  overflow: auto;
  resize: vertical;
}

.tutorial_noDalle .section4 textarea:focus {
  outline: 2px solid #c1ab86;
}

.tutorial_noDalle .section5 {
  width: 95%;
  display: flex;
  flex-direction: row;
  margin-left: 0.3125em;
}

.tutorial_noDalle .section5 .text {
  margin-bottom: 0.625em;
}

.tutorial_noDalle .section5 select {
  width: 35%;
  margin-left: 0.625em;
  border-radius: 0.25em;
  border: 1px solid #c1ab86;
  outline: 0 none;
  text-align: center;
  margin-right: 0.3125em;
}


.section05 {
  display: flex;
  align-items: center;
  gap: 10px;
}


.button-group {
  display: flex;
  gap: 10px;
}

button {
  padding: 10px 15px;
  border-radius: 1em;
  background: #eee7db;
  color: black;
  cursor: pointer;
  font-size: 1rem;
}

button.active {
  color: #fff;
  background: #A5778F;
}

.saveDiary {
  display: flex;
  flex-direction: column;
  align-content: center;
  justify-content: center;
  margin-top: 2rem;
  text-align: center;
  width: 120px;
  height: 40px;
  font-size: 1rem;
  font-weight: bold;
  color: #fff;
  border: #A5778F;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 1em; /* 기록하기 버튼과 간격 조정 */
}

.saveDiary button:hover {
  background: #8a5e72;
  color: #fff;
}
</style>
