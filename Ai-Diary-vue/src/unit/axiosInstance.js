// 📁 src/unit/axiosInstance.js

import axios from "axios";

const apiUrl = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080';
const recordUrl = import.meta.env.VITE_APP_RECORD_URL || 'https://justsaying.co.kr/#/pomoLogin';

const instance = axios.create({
    baseURL: apiUrl,
    withCredentials: true,
});

// 요청 인터셉터
instance.interceptors.request.use(
    config => {
        const token =
            localStorage.getItem("jwtToken") ||
            localStorage.getItem("accessToken") ||
            localStorage.getItem("kakaoAccessToken");

        console.log("@@@ axiosInstance Authorization:", token);

        if (token && !config.url.includes("/api/timer")) {
            config.headers.Authorization = token.startsWith("Bearer ")
                ? token
                : `Bearer ${token}`;
        }

        return config;
    },
    error => Promise.reject(error)
);

// 응답 인터셉터
instance.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem("refreshToken");
                const res = await instance.post("/api/auth/refresh", { refreshToken });


                const newAccessToken = res.data.accessToken;

                console.log(`250518_refreshToken`, newAccessToken);
                localStorage.setItem("jwtToken", newAccessToken);

                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return instance(originalRequest);
            } catch (e) {
                localStorage.clear();
                window.location.href = "/pomoLogin";
                return Promise.reject(e);
            }
        }

        return Promise.reject(error);
    }
);

export { apiUrl, recordUrl };
export default instance;
