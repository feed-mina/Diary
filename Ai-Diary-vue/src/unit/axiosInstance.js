//   src/unit/axiosInstance.js

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

        console.log("@@@ 최종 token:", token);
        console.log("axiosInstance jwtToken:", localStorage.getItem("jwtToken"));
        console.log("axiosInstance accessToken:", localStorage.getItem("accessToken"));
        console.log("axiosInstance kakaoAccessToken:", localStorage.getItem("kakaoAccessToken"));

        //  undefined나 "undefined"일 경우 무시
        if (token && token !== "undefined" && !config.url.includes("/api/timer")) {
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
        error.config && console.log("요청 실패 URL:", error.config.url);

        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // refreshToken은 HttpOnly 쿠키로 전송되므로 body는 비워도 됨 (백엔드가 쿠키에서 읽음)
                const res = await instance.post("/api/auth/refresh");
                // const res = await instance.post("/api/auth/refresh", { refreshToken });

                // const refreshToken = localStorage.getItem("refreshToken");
                const newAccessToken = res.data.accessToken;

                console.log(`  250527_refreshToken`, newAccessToken);
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
