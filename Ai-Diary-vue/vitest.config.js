import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  optimizeDeps: {
    exclude: ['vuetify'], // 문제를 일으키는 모듈 추가
  },
  test: {
    globals: true,
    environment: 'jsdom',
  },
});
