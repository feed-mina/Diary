<template>
    <div>
      <h1>ComponentC</h1>
      <p>Message: {{ message }}</p>
      <button @click="changeMessage">Change Message</button>
    </div>
  </template>
  
  <script>
  import { mapState, mapMutations } from 'vuex';
  
  export default {
    computed: {
      ...mapState(['message']), // Vuex의 상태를 가져옴
    },
    methods: {
      ...mapMutations(['setMessage']), // Vuex의 mutations 사용
      changeMessage() {
        this.setMessage('New Message from ComponentA');
      },
    },
  };
  </script>
  