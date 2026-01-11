<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-container">
      <header class="modal-header">
        <h2>{{ header }}</h2>
        <button class="close-button" @click="close">&times;</button>
      </header>
      <main class="modal-body">
        <slot></slot>
      </main>
      <footer class="modal-footer">
        <button class="modal-close-btn" @click="close">Close</button>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  props: {
    open: {
      type: Boolean,
      required: true,
    },
    close: {
      type: Function,
      required: true,
    },
    header: {
      type: String,
      default: "Modal Header",
    },
  },
  computed: {
    isOpen() {
      return this.open;
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: #fff;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease-out;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1em;
  border-bottom: 1px solid #ddd;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5em;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5em;
  cursor: pointer;
}

.modal-body {
  padding: 1em;
}

.modal-footer {
  padding: 1em;
  border-top: 1px solid #ddd;
  text-align: right;
}

.modal-close-btn {
  padding: 0.5em 1em;
  background: #007bff;
  border: none;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.modal-close-btn:hover {
  background: #0056b3;
}

@keyframes slideDown {
  from {
    transform: translateY(-20%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
