// main.js

import './assets/main.css';
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import { createVuetify } from 'vuetify';
import 'vuetify/styles';
import '@mdi/font/css/materialdesignicons.css';
import router from './router/index.js';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';


const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light',
        themes: {
            light: { dark: false, colors: {} },
            dark: { dark: true, colors: {} },
        },
    },
});

const app = createApp(App);

app.use(router);
app.use(vuetify);
app.use(createPinia());

app.mount('#app');
