// src/api/translatorApi.js
import axios from 'axios'


const translatorApi = axios.create({
    baseURL: '/api-translator', // 프록시 경로를 baseURL로
    withCredentials: true
})

export default translatorApi
