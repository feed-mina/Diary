import './assets/main.css'
import { createApp } from 'vue';
import App from './App.vue';
import { createVuetify } from 'vuetify';
import 'vuetify/styles'; // Vuetify 스타일
import '@mdi/font/css/materialdesignicons.css'; // 아이콘 폰트

import router from "./router/index.js"; // 라우터 설정이 있다면
import store from "./store/store.js"; // 상태 관리 라이브러리


import { aliases, mdi } from "vuetify/iconsets/mdi"; // 아이콘 설정 (옵션)
import * as components from "vuetify/components"; // Vuetify 컴포넌트 등록
import * as directives from "vuetify/directives"; // Vuetify 디렉티브 등록

const vuetify = createVuetify({
    components,
    directives,
});
const app = createApp(App);

app.use(router); // 라우터 추가
app.use(store); // 상태 관리 라이브러리 추가
app.use(vuetify); // Vuetify 등록

app.mount("#app"); // #app에 마운트
// 앱 마운트하기 : 엡 인스턴스는 .mount() 메서드가 호출될때까지 아무것도 렌더링하지 않는다 컨테이너가 될 실제 DOM앨리먼트 또는 셀렉터 문자열을 인자로 필요한다.
// 앱의 최상위 컴포넌트 컨텐츠는 컨테이너 엘리먼트 내에서 렌더링된다. 컨테이너 엘리먼트 자체는 앱의 일부로 간주되지 않는다.
// .mount() 메서드는 반드시 앱의 환경설정 및 에셋이 모두 등록 완룍된 후에 호출되어야 한다. 또한 에셋 등록 메서드와 달리 반환 값은 앱 인스턴스가 아닌 최상위 컴포넌트 인스턴스이다.
// DOM 내부의 최상위 컴포넌트 템플릿 : 빌드 과정 없이 Vue를 사용할때 마운트 컨테이너 내부에 직접 최상위 컴포넌트의 템플릿을 작성할 수 있다.

// 앱 인스턴스는 동일한 페이지 내 하나로 제한되지 않는다. createApp API를 사용하면 여러 VUE앱이 동일한 페이지에 공존할 수 있으며 각각은 구성 및 전역 자산에 대한 고유한 범위를 갖는다.

// 서버에서 렌더링 된 대규모 페이지의 HTML을 개선하고 특정부분을 제어하기 위해 Vue가 필요한 경우 페이지 전체를 단일 Vue 앱 인스턴스로 마운트하지 마십시오. 대신 여러 개의 작은 앱 인스턴스를 만들고 감당해야 하는 엘리먼트에 마운트한다.

// v-html은 XSS의 취약점이 쉽게 발생한다. htmlㅇ을 동적으로 렌더링하기 때문에 사용자가 제공한 컨텐츠에 절대 사영하지 않는다 

// 서ㅗㄱ성 속성 바인딩
// 이중 중괄호는 속성 내에서 사용할 수 없다 대신 cv0v0bindfmff를 사용한다 ㅍ-ㅠㅑㅜㅇ디렉티브는 엘리먼트의 ㅑㅇㅑㅇ

// 속성 바인딩 v-bind 디렉티브는 엘리멘트의 id속성을 컴포넌트의 dynamicId속성과 동기화된 상태로 유지하도록 vue에 지시한다 바인딩된 값이 null 또는 undefined이면 엘리먼트의 속성이 제거된 상태로 렌더링된다. 

// 단축 문법 v-bind는 매우 일반적으로 사용되기 때문에 전용 단축 문법이 있다. :id="dynamicId" :id로 시작하는 속성은 일반 html과 약간 다르게 보일 수 있지만 실제로는 유효한 속성명 문자열이며 펴vue를 지원하는 모든 브라우저에서 올바르게 파싱할수있다. 또한 최종 렌더링된 마크업에는 표시되지 않는다. 

// 하나의 표현식만 가능 각 바인딩에는 하나의 단일 표현식만 포함될수있다. 표현식은 값으로 평가할수 있어야 하는 코드조각이다 쉽게 말해 return뒤에 사용할 수 있는 코드여야 하는것이다.

// 바인딩표현식 내부에서 호출되는 함수는 컴포넌트가 업데이트 될때마다 호출되므로 데이터를 변경 이나 비동기 작업을 트리거하는 등의 부작용이 없어야 한다 

// 















