// 서버 주소를 환경에 따라 자동 설정
export const apiUrl = import.meta.env.MODE === 'development'
    ? 'http://localhost:8080'
    : 'http://justsaying.co.kr';
