// src/api/index.js
export const apiUrl = window.__ENV__?.VUE_APP_API_BASE_URL || "https://justsaying.co.kr";
export const recordUrl = window.__ENV__?.VUE_APP_RECORD_URL || 'https://justsaying.co.kr/#/pomoLogin';
export const imageUrl = window.__ENV__?.VUE_APP_IMAGE_BASE_URL || 'https://justsaying.co.kr/img';
export const cdnUrl = window.__ENV__?.VUE_APP_CDN_BASE_URL || 'https://cdn.justsaying.co.kr';
