import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
    plugins: [
        vue(),
        vueDevTools(),
        vuetify({autoImport: true}), // Vuetify 플러그인 추가
    ],
    server: {
        proxy: {
            '/api-translator': {
                target: 'http://127.0.0.1:8001',
                changeOrigin: true,
                rewrite: path => path.replace(/^\/api-translator/, '')
            }
        },
        port: 4000, // 포트 확인
    },
    base: '/', //루트(/) 경로 기준으로 정적 파일 전송 (s3)
    build: {
        outDir: 'dist',
    },// <-- S3에서 사용될 프로젝트 폴더 지정
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
        'process.env': process.env,
    },
})
