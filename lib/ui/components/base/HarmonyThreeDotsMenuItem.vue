<template>
  <div class="item-wrapper" :class="isInLoadingMode ? 'item-loading-mode' : ''" @click="onItemClick">
    <span v-show="isInLoadingMode" class="loader"></span>
    <p>{{caption}}</p>
  </div>
</template>

<script lang="ts" setup>

const emit = defineEmits<{
  (e: 'onItemClick'): void,
}>()

const props = defineProps({
  caption: String,
  isInLoadingMode: {
    type: Boolean,
    required: false,
    default: false
  }
})

const onItemClick = () => {
  if(!props.isInLoadingMode)
    emit("onItemClick")
}
</script>

<style scoped>

.item-wrapper {
  margin: 10px;
  display: flex;
  justify-content: center;
  cursor: pointer;
  border-radius: 10px;
  padding: 10px;
}

.item-wrapper:hover {
  background-color: var(--harmony-light-2);
}

.loader {
  width: 15px;
  height: 15px;
  border: 1px solid var(--harmony-light-3);
  border-bottom-color: transparent;
  border-radius: 50%;
  display: inline-block;
  box-sizing: border-box;
  animation: rotation 1s linear infinite;
  margin-right: 10px;
  margin-top: 5px
}

.item-loading-mode {
  cursor: not-allowed;
}

@keyframes rotation {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

</style>