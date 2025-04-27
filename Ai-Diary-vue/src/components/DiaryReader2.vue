<!-- DiaryReader.vue -->
<template>
  <div>
    <h2>오늘의 일기</h2>
    <p ref="diaryText">햄은 오늘도 최고야! 이 기능도 뚝딱 만들었어.</p>

    <div>
      <label for="voice">음성 선택:</label>
      <select id="voice" v-model="selectedVoiceName">
        <option v-for="voice in koreanVoices" :key="voice.name" :value="voice.name">
          {{ voice.name }}
        </option>
      </select>
    </div>

    <button :disabled="isReading" @click="readDiary">
      {{ isReading ? "읽는 중..." : "읽어줘!" }}
    </button>

    <div v-if="isReading" class="wave">음성을 재생 중입니다...</div>
  </div>
  <div>
    <input v-model="word1" placeholder="첫 번째 단어" />
    <input v-model="word2" placeholder="두 번째 단어" />
    <button @click="checkPronunciation">비교하기</button>
    <p>결과: {{ result }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const diaryText = ref(null)
const isReading = ref(false)
const selectedVoiceName = ref('')
const koreanVoices = ref([])
import axios from 'axios';

async function checkWords(word1, word2) {
  const response = await axios.post('http://EC2주소:포트/api/check', {
    word1: word1,
    word2: word2
  });
  console.log('라벤슈타인 거리:', response.data.distance);
}

const loadVoices = () => {
  const voices = window.speechSynthesis.getVoices()
  koreanVoices.value = voices.filter(v => v.lang.startsWith('ko'))
  if (koreanVoices.value.length > 0) {
    selectedVoiceName.value = koreanVoices.value[0].name
  }
}

// 브라우저가 voice를 늦게 로드할 수도 있어서 이벤트 등록
onMounted(() => {
  loadVoices()
  window.speechSynthesis.onvoiceschanged = loadVoices
})

const readDiary = () => {
  const text = diaryText.value?.innerText || ''
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'ko-KR'
  utterance.voice = koreanVoices.value.find(v => v.name === selectedVoiceName.value)
  isReading.value = true

  utterance.onend = () => {
    isReading.value = false
  }

  window.speechSynthesis.speak(utterance)
}
</script>

<style scoped>
.wave {
  margin-top: 10px;
  font-size: 14px;
  color: #FF5733;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
</style>
