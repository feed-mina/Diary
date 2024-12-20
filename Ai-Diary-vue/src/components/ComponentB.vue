<template>
  <div>
    <h1>ComponentB</h1>
    <p>Message from Store: {{ message }}</p>
    <button @click="updateMessageFromB">Update Message</button>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['message']), // Vuex 상태 가져오기
  },
  methods: {
    ...mapActions(['updateMessage']), // Vuex 액션 사용
    updateMessageFromB() {
      this.updateMessage('Updated Message from ComponentB');
    },
  },
};
</script>
