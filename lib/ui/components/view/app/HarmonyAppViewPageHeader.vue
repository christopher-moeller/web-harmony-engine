<template>
  <div class="header">
    <div class="left" style="width: 100%;">
      <div class="mobile-title-row">
        <div v-show="layoutState.windowSize.value.state === 'PC'">
          <slot name="subHeader"></slot>
        </div>
        <div class="action-slot-area" style="flex-grow: 1">
          <slot name="actionArea"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>

import useLayoutState from '~/composables/useLayoutState';

const layoutState = useLayoutState()

</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  grid-gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.header .left h1 {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--harmony-black);
}


.action-slot-area {
  display: flex;
  justify-content: right;
}


.mobile-title-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}
</style>