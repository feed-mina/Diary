import axios from 'axios'

let fastApiBaseUrl = '';

if (window.location.hostname === 'localhost') {
    // 로컬 개발 환경
    fastApiBaseUrl = 'http://localhost:8001';
} else {
    // 배포 서버 환경
    fastApiBaseUrl = 'https://justsaying.co.kr';
}

const translatorApi = axios.create({
    baseURL: fastApiBaseUrl
});

export default translatorApi
