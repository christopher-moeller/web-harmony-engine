<template>
  <ul v-if="isVisible" class="breadcrumb">
    <template v-for="(item, index) in items" :key="index">
      <NuxtLink :to="item.link" :class="isLastItem(index) ? 'active' : ''">
        {{ item.label }}
      </NuxtLink>
      <template v-if="!isLastItem(index)"> /</template>
    </template>
  </ul>
</template>

<script setup lang="ts">

import {BreadcrumbItem} from "~/utils/HarmonyTypes";

const props = defineProps({
  items: {
    type: Array as PropType<BreadcrumbItem[]>,
    required: true
  },
  visibleOnlyWithMoreThanTwoItems: {
    type: Boolean,
    required: false,
    default: true
  }
});

const isLastItem = (itemIndex: number) => itemIndex === props.items.length - 1

const isVisible = computed(() => props.visibleOnlyWithMoreThanTwoItems ? props.items.length >= 2 : true)
</script>

<style scoped>

.breadcrumb {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  grid-gap: 16px;
}

.breadcrumb a {
  color: var(--harmony-light-3);
}

.breadcrumb a.active {
  color: var(--harmony-primary);
}

</style>