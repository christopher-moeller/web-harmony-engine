<template>
  <client-only :key="formKey">
    <slot v-if="formLoaded"/>
  </client-only>
</template>

<script setup lang="ts">

import {PropType, ref} from "vue";
import {ResourceContext} from "~/composables/form/useResourceContext";
import {ApiLink} from "~/CoreApi";
import {ApiLinkResolver, EffectiveApiLink, HarmonyAxiosResponse} from "~/utils/HarmonyTypes";

const props = defineProps({
  resourceContext: {
    type: Object as PropType<ResourceContext>,
    required: true
  }
})

const formKey = ref(0)

const resourceContext:ResourceContext = props.resourceContext!

const generalApiLinkResolver:ApiLinkResolver = {
  resolveApiLink(apiLink: ApiLink): EffectiveApiLink {
    return {
      link: apiLink.link!.replace("{id}", resourceContext.getResourceId()),
      requestMethod: apiLink.requestMethod!
    }
  }
}

resourceContext.getContextConfig().getTemplateLinkResolver = generalApiLinkResolver
resourceContext.getContextConfig().getByIdLinkResolver = generalApiLinkResolver
resourceContext.getContextConfig().createNewLink = generalApiLinkResolver
resourceContext.getContextConfig().updateLink = generalApiLinkResolver
resourceContext.getContextConfig().deleteLinkResolver = generalApiLinkResolver

resourceContext.getContextConfig().afterSaveListeners.push({
  async afterSave(response: HarmonyAxiosResponse, isNew: boolean): Promise<void> {
    if(response.success && !isNew) {
      await resourceContext.hardFetchApiState()
    }
  }
})

resourceContext.getContextConfig().dataLoadedListeners.push({
  async dataLoaded(): Promise<void> {
    formKey.value = formKey.value + 1 // this forces the form to reload
  }
})

const formLoaded = ref(false)

if(process.client) {
  await resourceContext.fetchIfNotInLocalStore()
  formLoaded.value = true
}


</script>

<style scoped>

</style>