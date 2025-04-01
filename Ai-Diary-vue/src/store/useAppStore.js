// 테마 관련 코드 제거
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
    state: () => ({
        isDarkMode: false
    }),
    actions: {

        toggleDarkMode() {
            this.isDarkMode = !this.isDarkMode
            document.documentElement.classList.toggle('dark', this.isDarkMode)
            localStorage.setItem('isDarkMode', this.isDarkMode)

        }
    }
})
