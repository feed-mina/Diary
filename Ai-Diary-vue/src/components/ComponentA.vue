<script setup>

import store from '../store/store.js'
</script>

<template>
  <div>
    <button @click="store.increment()">
      A 컴포넌트에서: {{ store.count }}
    </button>
  </div>
</template>