<template>
  <NuxtLink v-if="link" :to="link" class="link">{{internalCaption}}</NuxtLink>
  <NuxtLink v-else-if="page" :to="{name: page}" class="link">{{internalCaption}}</NuxtLink>
  <NuxtLink v-else to="#" class="link">{{internalCaption}}</NuxtLink>
</template>

<script setup lang="ts">

const props = defineProps({
  caption: {
    type: String,
    required: false
  },
  link: {
    type: String,
    required: false
  },
  page: {
    type: String,
    required: false
  }
});

const internalCaption = computed(() => props.caption ? props.caption : props.link)

</script>

<style scoped>

.link {
  color: var(--harmony-primary);
}

</style>