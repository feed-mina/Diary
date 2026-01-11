<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/api/translatorApi.js'

//  DiaryView에서 content 전체 받아오기
const props = defineProps({
  content: {
    type: Object,
    required: true
  }
})

console.log("API baseURL:", api.defaults.baseURL)
//  상태 정의
const fullText = ref('')
const japaneseDiary = ref('')
const ttsAudioUrl = ref('')
const japaneseLines = ref([])   // 전체 일본어 번역 줄 나누기
const titleAndContentText = ref('') // tts 일본어 번역 줄 나누기
const titleAndContentLines = ref([])
const titleLineIndex = ref(0)
const currentLineIndex = ref(-1) // 읽히는 줄 번호

// 로딩 상태
const isTranslating = ref(false)
const isReading = ref(false)
// 감정상태 매칭
const emotionItems = [
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
]

//  감정상태 숫자 ➔ 텍스트 변환
const getEmotionText = (emotionValue) => {
  if (!emotionValue) return "기록 없음"
  const found = emotionItems.find(item => item.value === emotionValue.toString())
  return found ? found.text : "기록 없음"
}

// 전체 일기 텍스트 만들기
const makeFullText = () => {
  fullText.value = `
    날짜: ${props.content.date || ''}
    작성자: ${props.content.author || ''}
    제목: ${props.content.title || ''}
    내용: ${props.content.content || ''}
    감정 상태: ${getEmotionText(props.content.emotion)}
    태그: ${props.content.tag1 || ''} ${props.content.tag2 || ''} ${props.content.tag3 || ''}
  `
  // 제목과 내용만 따로 만들기

  titleAndContentText.value = `
제목: ${props.content.title || ''}
내용: ${props.content.content || ''}
  `.trim()

  titleAndContentLines.value = titleAndContentText.value.split('\n')

  // "제목:" 이 들어있는 줄이 몇 번째 줄인지 찾아서 저장
  titleLineIndex.value = titleAndContentLines.value.findIndex(line => line.startsWith('제목:'))
}

//  번역 함수
const translateDiary = async () => {
  try {
    console.log('translateDiary 시작')
    isTranslating.value = true
    const response = await api.post('/translate_only', { content: fullText.value });

    japaneseDiary.value = response.data.translated_text
    japaneseLines.value = japaneseDiary.value.split('\n')    // 일본어 전체 일기 줄 나누기

    titleAndContentLines.value = []
    let isTitleOrContent = false

    for (const line of japaneseLines.value) {
      if (line.startsWith('タイトル') || line.startsWith('コンテンツ')) {
        isTitleOrContent = true
      }
      if (line.startsWith('感情のステータス') || line.startsWith('タグ')) {
        isTitleOrContent = false
      }
      if (isTitleOrContent) {
        titleAndContentLines.value.push(line)
      }
    }
    titleLineIndex.value = titleAndContentLines.value.findIndex(line => line.startsWith('タイトル'))
  } catch (error) {
    console.error('번역 실패:', error)
  }finally {
    isTranslating.value = false
  }
}

//  읽기(TTS) 함수
const readJapanese = async () => {
  if (!titleAndContentLines.value.length) return

  try {
    let lineIndex = 0
    currentLineIndex.value = 0
    isReading.value = true

    const readNextLine = async () => {
      if (lineIndex >= titleAndContentLines.value.length) {
        isReading.value = false
        return
      }

      const lineText = titleAndContentLines.value[lineIndex]
      console.log("읽을 줄:", lineText);
      const response = await api.post('/tts_only', { text: lineText });
      ttsAudioUrl.value = response.data.tts_audio_url;

      const audio = new Audio(ttsAudioUrl.value);
      audio.addEventListener("canplaythrough", () => {
        audio.play();
      });
      audio.addEventListener("error", (e) => {
        console.error("오디오 재생 실패:", e);
      });


      // 줄 길이에 따라 대기 시간 계산
      const baseTimePerCharacter = 150  // 글자당 150ms
      const extraDelay = lineText.length * baseTimePerCharacter

      audio.onended = () => {
        setTimeout(() => {
          lineIndex++
          currentLineIndex.value = lineIndex
          readNextLine()
        }, extraDelay)
      }
    }

    await readNextLine()
  } catch (error) {
    console.error('TTS 실패:', error)
    isReading.value = false
  }
}

//  처음 시작할 때 바로 번역 준비
onMounted(async () => {
  makeFullText()
  await translateDiary()
})
</script>

<template>
  <div style="margin-top: 20px;">
    <div
        id="loading"
        @click="translateDiary"
        :disabled="isTranslating"
    >
      <span v-if="isTranslating" class="spinner"></span>
      {{ isTranslating ? "번역 중..." : "번역 완료" }}
    </div>

    <div v-if="japaneseDiary" style="margin-top: 20px;">
      <!-- 전체 일본어 번역 카드 -->
      <div class="card">
        <h3>📝 일본어 번역 결과</h3>
        <pre>{{ japaneseDiary }}</pre>
      </div>

      <!-- 읽히는 줄만 하이라이트 카드 -->
      <div class="card">
        <h3>🎵 읽히는 부분 (하이라이트)</h3>
        <div class="diary-highlight-text">
          <div v-for="(line, index) in titleAndContentLines" :key="index">
            <span
                :class="[
                { highlight: index === currentLineIndex },
                { 'title-line': index === titleLineIndex }
              ]"
            >
              {{ line }}
            </span>
          </div>
        </div>

        <button id="readButton" @click="readJapanese" :disabled="isReading">
          {{ isReading ? "읽는 중..." : "읽어줘! (내용만)" }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
p {
  font-size: 16px;
  margin-top: 10px;
}
pre {
  white-space: pre-wrap;
  font-size: 16px;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
}

#loading {
   display: inline-flex;
   align-items: center;
   gap: 8px;
   padding: 10px 20px;
   font-size: 1rem;
  font-weight: bold;
   border-radius: 8px;
   cursor: pointer;
   transition: background-color 0.3s;
   text-align: center;
 }

/* 로딩 아이콘 스타일 */
.spinner {
  width: 2rem;
  height: 2rem;
  border: 3px solid white;
  border-top: 3px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 회전 애니메이션 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.card {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 1.5rem;
  margin-top: 20px;
  border-radius: 12px;
}
.diary-highlight-text {
  font-size: 1rem;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  margin-top: 10px;
}
.highlight {
  background-color: #ffe680;
  font-weight: bold;
}

.title-line {
  font-weight: bold;
  font-size: 18px;
  color: #2c3e50;
}
#readButton{
  font-size: 1rem;
  font-weight: bold;
  color: white;
  margin-top: 10px;
  background-color: #4CAF50;
}
</style>
