<template>
  <BreadcrumbNavigation :items="items" :visible-only-with-more-than-two-items="visibleOnlyWithMoreThanTwoItems"/>
</template>

<script setup lang="ts">

import BreadcrumbNavigation from "~/components/utils/BreadcrumbNavigation.vue";
import useNavigationTree from "~/composables/useNavigationTree";
import useRouterUtils from "~/composables/useRouterUtils";
import {BreadcrumbItem} from "~/utils/HarmonyTypes";

const props = defineProps({
  visibleOnlyWithMoreThanTwoItems: {
    type: Boolean,
    required: false,
    default: true
  }
});

const routerUtils = useRouterUtils()
const items = ref<BreadcrumbItem[]>(useNavigationTree().buildGenericBreadcrumbItemsByPageId(<string>routerUtils.getCurrentRoute().value.name))

</script>

<style scoped>

</style>