<script>
import Cookies from 'universal-cookie'; // universal-cookie
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import { ref, reactive } from 'vue';



export default {
  name: 'DiaryWriting', // 다중 단어 이름으로 변경
  components: {
    Datepicker,
  },
    setup() {

    const diaryContent =  ref({
      date:"",
      author:"",
      title:"",
      tags:({ tag1: "", tag2: "", tag3: "" }),
      emotion:"",
      content:"",
      hidden:true,
    });

    const emotionItems = [
      { text: "😁 I feel good", value: "1" },
      { text: "😂 Oh, That's so funny", value: "2" },
      { text: "😫 What should I do?!", value: "3" },
      { text: "😒 unpleasant, boring", value: "4" },
      { text: "😤 How dare you", value: "5" },
      { text: "😡 Angry", value: "6" },
      { text: "🤯 I wanna get outta here...", value: "7" },
      { text: "💖 Love", value: "8" },
      { text: "🤕 Not in a good condition", value: "9" },
      { text: "💙 I feel blue", value: "10" }
    ];

  const saveDiary = () => {
  console.log("Diary saved");
  };

    return {
      emotionItems,
      diaryContent,
      saveDiary
    };
  },
}
</script>


<template>
  <div class="diaryWriting">
    <div class="diaryWriting_content">
      <div class="diaryTuto">
        <div class="diaryWriting_container">
          <form>
            <div class="diaryWritingTitle">
                <span class="diaryWritingHighlight">
                </span>
            </div>
            <div class="diaryWriting_noDalle">
              <div class="section0">
                <div><span>오늘 날짜</span>
                  <div>
                    <Datepicker
                    v-model="diaryContent.date"
                    :format="'YYYY-MM-DD'"
                    :auto-apply="true"
                    :locale="'ko'"
                  />

                <div v-if="diaryContent.date">선택된 날짜: {{ diaryContent.date }}</div>

                  </div>
                </div>
              </div>
              <div class="section01">
                <div>
                    <div>
                      <label for="author">작성자&nbsp;&nbsp;&nbsp;</label>
                  <input type="text" class="author" id="author" name="author" v-model="diaryContent.author" placeholder=""  />
                    </div>
                    <div class="titleSc">
                      <label for="title">제목&nbsp;&nbsp;&nbsp;</label>
                  <input type="text" class="title" id="title" name="title" v-model="diaryContent.title" placeholder="" />
                    </div>
                </div>
              </div>
              <!--section2-->
              <div class="section2">
                <div class="text">
                    <span>오늘의 감정을 태그로 입력하세요.</span>
                </div>
                <div class="tags">
                    <input type="text" id="tag1" name="tag1" v-model="diaryContent.tags.tag1" placeholder="tag1" />

                    <input type="text" id="tag2" name="tag2" v-model="diaryContent.tags.tag2" placeholder="tag2" />

                    <input type="text" id="tag3" name="tag3" v-model="diaryContent.tags.tag3" placeholder="tag3" />
                  <!-- <button type="button" class="aiButton">
                      <span>AI 이미지 생성</span>
                  </button> -->
                </div>
              </div>
              <!--section3-->
              <div class="section3">
                <div class="text">
                      <span>감정지수를 선택하세요.</span>
                </div>
                <div class="selectBox">
                  <v-select
                  v-model="diaryContent.emotion"
                  :items="emotionItems"
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
                      <span>본문</span>
                </div>
                <textarea v-model="diaryContent.content" rows="3" class="content" name="content" id="content" ></textarea>
              </div>
              <!--section05-->
              <div class="section05">
                      <span>🔎</span>
                    <select v-model="diaryContent.hidden" id="hidden" required>
                      <option value="true">
                        숨기기
                      </option>
                      <option value="false">
                        보여주기
                      </option>
                    </select>
                    <div>
                      <button type="button" @click="saveDiary">일기장완료</button>
                      <button type="button" @click="goDiary">뒤로가기</button>
                    </div>
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
.diaryWriting{
    width: 99%;
    height: 75%;
    min-width: 25em;
    height: 100%;
    margin: 0 auto;
    border-radius: 0.625em;
    overflow: hidden;
}

.diaryWriting_content {
    height: 100%;
    width: 99%;
    top: 1.875em;
    right: 0;
    bottom: 33.75em;
    left: 3.75em;
    background-size: 30px 30px;
  }

  .diaryTuto {
    padding-top: 1.5em;
    width: 100%;
    height: 100%;
    font-size: 2vmin;
  }
  .titleSc{
    padding-top:2rem;
  }
  .diaryTuto input,
  textarea,
  button {
    border-radius: 0.3125em;
    background: #eee7db;
    border: 0 solid black;
    font-size: 0.9375em;
  }
  .diaryWriting_container {
    /* border: 1px solid #00fa9a; */
    margin: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: row;
  }

  .diaryWritingTitle {
    font-size: 2rem;
    text-align: center;
    font-weight: bold;

  }
  .diaryWritingTitle {
    box-shadow: inset 0 -23px 0 #e89b3d;
    display: inline;
  }

  .diaryWriting_container form {
    width: 100%;
    height: 100%;
  }
  .diaryWriting_noDalle {
    width: 65%;
    height: 100%;
    margin: 0em;
    float: left;
  }
  .diaryWriting_noDalle input,
  select,
  button,
  option {
    height: 1.875em;
  }

  .diaryWriting_container input:focus {
    outline: 2px solid #c1ab86;
    transition: 0.1s;
  }


  .diaryWriting_noDalle .section0 {
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }
  .diaryWriting_noDalle .section01 {
    margin-top: 0.125em;
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }

  .diaryWriting_noDalle .section01 input {
    width: 40%;
    text-align: center;
  }

 .diaryWriting_noDalle .section1 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 42%;
  }

 .diaryWriting_noDalle .section2 {
    width: 100%;
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section2 .tags {
    padding-top: 0.625em;
    width: 95%;
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
  }
 .diaryWriting_noDalle .section2 input {
    text-align: center;
    width: 25%;
    margin: 0.3125em;
    caret-color: #604e2e;
  }
 .diaryWriting_noDalle .section2 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 27%;
  }
 .diaryWriting_noDalle .section2 button {
    width: 25%;
    margin: 0.3125em;
    height: 2em;
    background: #c1ab86;
  }
 .diaryWriting_noDalle .section2 button:hover {
    transition: 0.3s;
    opacity: 1;
    background: #604e2e;
    color: white;
    width: 30%;
  }
 .diaryWriting_noDalle .section3 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
  }
 .diaryWriting_noDalle .section3 .text {
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section3 select {
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    width: 35%;
    height: 2.5em;
  }

 .diaryWriting_noDalle .section3 .selectBox .v-select {
    background: transparent;
    outline: 0 none;
    padding: 0 5px;
    position: relative;
    z-index: 3;
  }
 .diaryWriting_noDalle .section3 .selectBox .v-select option {
    color: #c1ab86;
    padding: 0.1875em 0;
    font-size: 1em;
    border-radius: 0.25em;
    text-align: center;
  }
 .diaryWriting_noDalle .section3 .selectBox .icoArrow {
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

 .diaryWriting_noDalle .section3 .selectBox .icoArrow img {
    width: 50%;
    transition: 0.3s;
  }

 .diaryWriting_noDalle .section3 .selectBox .v-select:focus + .icoArrow img {
    transform: rotate(180deg);
  }

 .diaryWriting_noDalle .section4 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
    margin-left: 0.3125em;
    width: 95%;
    height: 50%;
  }
 .diaryWriting_noDalle .section4 .text {
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section4 textarea {
    width: 100%;
    height: 100%;
    padding: 0.625em;
    font-size: 1.25em;
    overflow: auto;
    resize: vertical;
  }
 .diaryWriting_noDalle .section4 textarea:focus {
    outline: 2px solid #c1ab86;
  }

 .diaryWriting_noDalle .section5 {
    width: 95%;
    display: flex;
    flex-direction: row;
    margin-bottom: 1.25em;
    margin-left: 0.3125em;
  }
 .diaryWriting_noDalle .section5 .text {
    margin-bottom: 0.625em;
  }

 .diaryWriting_noDalle .section5 select {
    width: 35%;
    height: 2.5em;
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    margin-right: 0.3125em;
  }

 .diaryWriting_noDalle .section5 button {
    width: 35%;
    margin-right: 0.3125em;
    text-align: center;
  }

 .diaryWriting_noDalle .section5 button:hover {
    width: 40%;
    opacity: 1;
    background: #604e2e;
    color: white;
    transition: 0.3s;
  }

</style>
