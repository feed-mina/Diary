<script setup>
import { ref } from 'vue'
import api from '@/api/translatorApi.js'

const japaneseText = ref('これはテストです。')
const isPlaying = ref(false)

const playBlobTTS = async () => {
  if (!japaneseText.value.trim()) return
  isPlaying.value = true
  try {
    const response = await api.post('/tts_blob', { text: japaneseText.value }, { responseType: 'blob' })
    const audioBlob = new Blob([response.data], { type: 'audio/mpeg' })
    const audioUrl = URL.createObjectURL(audioBlob)
    const audio = new Audio(audioUrl)
    audio.play()
    audio.onended = () => {
      isPlaying.value = false
    }
    audio.onerror = (e) => {
      console.error('오디오 재생 실패:', e)
      isPlaying.value = false
    }
  } catch (error) {
    console.error('TTS 요청 실패:', error)
    isPlaying.value = false
  }
}
</script>

<template>
  <div class="blob-tts-wrap">
    <h3>🗣 Blob 방식 TTS 테스트</h3>
    <textarea v-model="japaneseText" rows="3" class="tts-input" placeholder="일본어 문장을 입력하세요" />
    <button @click="playBlobTTS" :disabled="isPlaying">
      {{ isPlaying ? '재생 중...' : '재생하기' }}
    </button>
  </div>
</template>

<style scoped>
.blob-tts-wrap {
  margin-top: 2rem;
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 12px;
}
.tts-input {
  width: 100%;
  font-size: 1rem;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}
button {
  font-size: 1rem;
  font-weight: bold;
  background-color: #4CAF50;
  color: white;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
button:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}
</style>
