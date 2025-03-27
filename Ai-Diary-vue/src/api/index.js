// src/api/index.js
// export const apiUrl = import.meta.env.VITE_APP_API_BASE_URL;
export const apiUrl = window.__ENV__?.VUE_APP_API_BASE_URL || "http://localhost:8080";
