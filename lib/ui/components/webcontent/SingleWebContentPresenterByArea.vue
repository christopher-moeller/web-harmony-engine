<template>
  <web-content-component v-model="computedContent" :context="editContext"/>
</template>

<script setup lang="ts">

import {WebContentContext} from "~/utils/HarmonyTypes";
import WebContentComponent from "~/components/webcontent/views/WebContentComponent.vue";

const props = defineProps({
  areaName: {
    type: String,
    required: true
  }
})

const coreApi = useCoreApi()
const {data: webContent} = await coreApi.api().getWebContentApi().loadUniqueWebContentByArea(props.areaName)

const editContext = ref<WebContentContext>({
  isInEditMode: false,
  hoverComponentStack: [],
  id: webContent.id+""
})

const computedContent = ref(webContent.content)

</script>

<style scoped>

</style>