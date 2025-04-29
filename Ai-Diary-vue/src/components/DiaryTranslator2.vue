<template>
  <div class="tts-test-container">
    <h2>🧪 TTS Blob 테스트</h2>
    <textarea v-model="text" placeholder="읽어줄 문장을 입력하세요" />
    <button @click="playAudio" :disabled="loading">
      {{ loading ? '재생 중...' : '재생하기' }}
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const text = ref('これはテストです。')
const loading = ref(false)

const playAudio = async () => {
  if (!text.value.trim()) return alert('텍스트를 입력해주세요.')

  loading.value = true
  try {
    const res = await fetch('http://localhost:8001/tts_blob', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: text.value })
    })

    if (!res.ok) throw new Error('TTS 요청 실패')

    const blob = await res.blob()
    const audioUrl = URL.createObjectURL(blob)
    const audio = new Audio(audioUrl)

    audio.onended = () => {
      loading.value = false
      URL.revokeObjectURL(audioUrl)
    }

    audio.onerror = (e) => {
      console.error('오디오 재생 실패:', e)
      loading.value = false
    }

    audio.play()
  } catch (err) {
    console.error(err)
    alert('TTS 재생 중 오류가 발생했어요.')
    loading.value = false
  }
}
</script>

<style scoped>
.tts-test-container {
  margin-top: 2rem;
  background: #fefefe;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
textarea {
  width: 100%;
  min-height: 80px;
  padding: 0.5rem;
  font-size: 1rem;
  margin-bottom: 1rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  resize: vertical;
}
button {
  padding: 0.6rem 1.2rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  background-color: #4caf50;
  color: white;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
