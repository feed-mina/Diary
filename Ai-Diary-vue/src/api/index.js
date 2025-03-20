export const apiUrl = import.meta.env.MODE === 'development'
    ? 'http://localhost:8080'
    : (window.location.protocol === 'https:' ? 'https://justsaying.co.kr' : 'http://justsaying.co.kr');
