// /public/config.js
const hostname = window.location.hostname;

let apiUrl = '';

if (hostname === 'localhost') {
    apiUrl = 'http://localhost:8080';
} else {
    apiUrl = 'https://justsaying.co.kr';
}

let base = 'https://justsaying.co.kr';
if (hostname === 'localhost') {
    base = 'http://localhost:4000';
}
window.__ENV__ = {
    VUE_APP_API_BASE_URL: apiUrl,
    VUE_APP_IMAGE_BASE_URL: `${base}/img`,
    VUE_APP_CDN_BASE_URL: base.includes('localhost') ? `${base}/cdn` : 'https://cdn.justsaying.co.kr',
    VUE_APP_RECORD_URL: `${base}/#/pomoLogin`
};
