const hostname = window.location.hostname;

let apiUrl = '';

if (hostname === 'localhost') {
    apiUrl = 'http://localhost:8080';
} else {
    apiUrl = 'https://justsaying.co.kr';
}

window.__ENV__ = {
    VUE_APP_API_BASE_URL: apiUrl
};
