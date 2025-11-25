<template>
  <div class="panel">
    <div class="inner-area">
      <div>
        <h2 v-if="caption" class="caption">{{caption}}</h2>
      </div>
      <div>
        <slot name="action"></slot>
      </div>
    </div>
    <slot name="default"></slot>
  </div>
</template>

<script lang="ts" setup>

const props = defineProps({
  caption: {
    type: String,
        required: false
  }
})
</script>

<style scoped>

.panel {
  padding: 20px 20px;
  background-color: var(--harmony-light-1);
  border-radius: 25px;
}

.caption {
  margin-bottom: 40px;
}

.inner-area {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

</style>