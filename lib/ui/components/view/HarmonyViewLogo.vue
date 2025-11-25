<template>
  <a href="/" class="logo">
    <img src="/img/logo-generic.svg" alt="" class="logo-img"/>
    <div class="logo-name"><span>{{name}}</span><template v-if="isInAppMode">App</template></div>
  </a>
</template>

<script setup lang="ts">

defineProps({
  name: {
    type: String,
    required: true
  },
  isInAppMode: {
    type: Boolean,
    required: true
  }
})

</script>

<style scoped>

.logo-img {
  min-width: 40px;
  max-width: 40px;
  margin-left: 0.5em;
  margin-right: 0.5em;
}

.logo{
  font-size: 24px;
  font-weight: 700;
  height: 56px;
  display: flex;
  align-items: center;
  color: var(--harmony-primary);
  z-index: 500;
  padding-bottom: 20px;
  box-sizing: content-box;
}

.logo .logo-name span{
  color: var(--harmony-black);
  white-space: nowrap;
}

</style>