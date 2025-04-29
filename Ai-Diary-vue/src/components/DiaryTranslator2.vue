
<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/api/translatorApi.js'

// ✅ DiaryView에서 내용 받을 준비
const props = defineProps({
  content: {
    type: Object,
    required: true
  }
})


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
]


const fullText = ref('')
const japaneseDiary = ref('')
const ttsAudioUrl = ref('')

const japaneseLines = ref([])
const contentLines = ref([])

const currentLineIndex = ref(-1) 

const getEmotionText = (emotionValue) => {
  if (!emotionValue) return "기록 없음"
  const found = emotionItems.find(item => item.value === emotionValue.toString())
  if (!emotionValue || emotionValue === "0") return "기록 없음"
  return found ? found.text : "기록 없음"
}
// 일기 전체를 하나로 합치는 함수
const makeFullText = () => {
  fullText.value = `
    날짜: ${props.content.date || ''}
    작성자: ${props.content.author || ''}
    제목: ${props.content.title || ''}
    내용: ${props.content.content || ''}
    감정 상태: ${getEmotionText(props.content.emotion)}
    태그: ${props.content.tag1 || ''} ${props.content.tag2 || ''} ${props.content.tag3 || ''}
  `
}


// props가 바뀌면 다시 번역
watch(() => props.content, async (newContent) => {
  if (newContent) {
    await translateDiary(newContent)
  }
})

const translateDiary = async () => {
  try {
    console.log('translateDiary 시작')
    const response = await api.post('/translate_only', {
      content: fullText.value  // ✅ 전체 diary text를 보내기
    })

    japaneseDiary.value = response.data.translated_text
    japaneseLines.value = japaneseDiary.value.split('\n')
    // 내용(content)만 줄별로 따로 나누기
    contentLines.value = (props.content.content || '').split('\n')
  } catch (error) {
    console.error('번역 실패:', error)
  }
}

// 일본어로 읽어줄 때는 "내용"만!
const readJapanese = async () => {
  if (!contentLines.value.length) return

  try {
    const response = await api.post('/tts_only', {
      text: props.content.content
    })

    ttsAudioUrl.value = response.data.tts_audio_url.replace('http://127.0.0.1:8001', '');

    const audio = new Audio(ttsAudioUrl.value)

    let lineIndex = 0
    currentLineIndex.value = 0

    // 오디오 시작할 때
    audio.play()

    // 2초마다 다음 줄로 넘어가는 타이머 (임시)
    const interval = setInterval(() => {
      if (lineIndex < contentLines.value.length - 1) {
        lineIndex++
        currentLineIndex.value = lineIndex
      } else {
        clearInterval(interval)  // 다 읽으면 멈춰!
      }
    }, 2000)  // 2초마다 한 줄
  } catch (error) {
    console.error('TTS 실패:', error)
  }
}
// 컴포넌트가 처음 뜨면 바로 번역하기
onMounted(async () => {
  makeFullText()
  await translateDiary()
})

</script>


<template>
  <div style="margin-top: 20px;">
    <button @click="translateDiary">일본어로 번역하기</button>

    <div v-if="japaneseDiary" style="margin-top: 20px;">
      <h3>📝 일본어 번역 결과</h3>
<!--      <pre>{{ japaneseDiary }}</pre>-->
      <div class="diary-japanese-text">
        <div v-for="(line, index) in contentLines" :key="index">
    <span :class="{ highlight: index === currentLineIndex }">
      {{ line }}
    </span>
        </div>
      </div>
      <button @click="readJapanese">읽어줘! (내용만 읽기)</button>
    </div>
  </div>
</template>


<style scoped>
p {
  font-size: 16px;
  margin-top: 10px;
}
button {
  margin-top: 10px;
  padding: 8px 16px;
}
pre {
  white-space: pre-wrap; /* 줄 바꿈을 자연스럽게! */
  font-size: 16px;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
}
.diary-japanese-text {
  font-size: 16px;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
}

.highlight {
  background-color: #ffe680; /* 하이라이트 색 */
  font-weight: bold;
}

</style>
