import { reactive } from "vue";
import { createStore } from "vuex";

const store = createStore({
  state: {
    message: "Hello Vuex",
  },
  mutations: {
    setMessage(state, newMessage) {
      state.message = newMessage;
    },
  },
  actions: {
    updateMessage({ commit }, newMessage) {
      commit("setMessage", newMessage);
    },
  },
  getters: {
    getMessage(state) {
      return state.message;
    },
  },
  count: 0,
  increment() {
    this.count++;
  },
});

export default store;
