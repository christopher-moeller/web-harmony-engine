<template>
  <div class="collapsible-container" :style="withBorders ? '' : 'border: none;'">
    <button @click="toggleContent" class="collapsible-button">
      <span :class="{'arrow-down': isOpen, 'arrow-right': !isOpen}" class="arrow"></span>
      {{caption}}
    </button>
    <div v-show="isOpen" class="collapsible-content" :style="withBorders ? '' : 'padding: 0;'">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps({
  caption: {
    type: String,
    required: true
  },
  withBorders: {
    type: Boolean,
    required: false,
    default: true
  }
})
const isOpen = ref(false);

const toggleContent = () => {
  isOpen.value = !isOpen.value;
};
</script>

<style scoped>
.collapsible-container {
  border: 1px solid #ccc;
  border-radius: 20px;
  background-color: var(--harmony-light-1);
}

.collapsible-button {
  width: 100%;
  padding: 10px;
  color: var(--harmony-black);
  background-color: var(--harmony-light-1);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  text-align: left;
  font-size: 1rem;
}

.collapsible-button:focus {
  outline: none;
}

.collapsible-content {
  padding: 15px;
  border-top: 1px solid #ccc;
  border-radius: 0 0 50px 50px;
  animation: slide-down 0.8s ease-out;
}

@keyframes slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.arrow {
  color: black;
  border: solid white;
  border-width: 0 3px 3px 0;
  display: inline-block;
  padding: 3px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.arrow-right::before {
  content: '►';
  font-size: 12px;
}

.arrow-down::before {
  content: '▼';
  font-size: 12px;
}

</style>