/* eslint-env node */
module.exports = {
    env: {
        es2021 : true, // Node.js 전역 객체 활성화
        browser : true, // 브라우저 전역 객체 활성화 (선택사항)
    },
    globals: {
        process:"readonly", //process를 글로벌 객체로 허용
        module:"readonly", // module을 글로벌로 허용
    },
    extends: [
        "plugin:vue/vue3-essential",
        "eslint:recommended",
        "@vue/eslint-config-prettier",
    ],
    rules:{
        "no-undef":"off", // 정의되지 않은 변수 오류 방지
    },
};