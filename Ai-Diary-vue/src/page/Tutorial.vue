<script>
 
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
                <div class="tutorialHighlight">
                  <img class="tutorialImg" src="/img/jingu2.svg" alt="dino_diary" />
                </div>
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


</style>
