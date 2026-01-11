import vue from 'eslint-plugin-vue';
import parser from 'vue-eslint-parser';

export default [
    {
        ignores: ['node_modules/**'],
    },
    {
        files: ['**/*.vue', '**/*.js'], // 여기에서 검사할 파일 확장자 지정
        languageOptions: {
            parser, // vue-eslint-parser 사용
            ecmaVersion: 2020,
            sourceType: 'module',
        },
        plugins: {
            vue,
        },
        rules: {
            'vue/no-unused-vars': 'warn',
            'vue/no-multiple-template-root': 'off', // Vue 3에서는 필요 없음
        },
    },
];
