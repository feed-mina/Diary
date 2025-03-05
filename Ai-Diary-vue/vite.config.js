import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    vuetify({ autoImport: true }), // Vuetify 플러그인 추가
  ],
  server: {
    port: 4000, // 포트 확인
  },
  base: '/', // 기본 경로 설정
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.spec.{js,ts,jsx,tsx}'], // 테스트 파일 포함 경로 지정
  }, 
  define: {
    'process.env': {}, // Node.js에서 사용하는 process.env를 빈 객체로 처리
  },
})
