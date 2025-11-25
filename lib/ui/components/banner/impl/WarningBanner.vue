<template>
  <div id="banner">
    <i class='bx bx-check-circle'></i>
    <p>{{banner.message}}</p>
  </div>
</template>

<script setup lang="ts">

import {PropType} from "vue";
import {Banner} from "~/store/bannerStore";

const props = defineProps({
  banner: {
    type: Object as PropType<Banner>,
    required: true
  }
})

</script>

<style scoped>

#banner {
  background-color: var(--harmony-warning);
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  color: var(--harmony-black);
  padding: 0.5em 1em;
  max-width: 90vw;
}

#banner p {
  margin-left: 1em;
  font-size: 16px;
  color: var(--harmony-black);
  word-break: break-word;
}

</style>