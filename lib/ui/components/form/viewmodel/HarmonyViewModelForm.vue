<template>
  <client-only :key="formKey">
    <slot v-if="!errorState"/>
    <slot v-else name="error">
      <harmony-panel :caption="i18n.translate('Error').build()" class="panel">
        <p>{{i18n.translate("Data could not be loaded").build()}}</p>
      </harmony-panel>
    </slot>
  </client-only>
</template>

<script setup lang="ts">

import {onUnmounted, PropType, ref} from "vue";
import {ViewModelContext} from "~/composables/form/useViewModelContext";
import {ApiLinkResolver, HarmonyAxiosResponse} from "~/utils/HarmonyTypes";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";

const props = defineProps({
  viewModelContext: {
    type: Object as PropType<ViewModelContext>,
    required: true
  },
  loadLinkResolver: {
    type: Object as PropType<ApiLinkResolver>,
    required: false
  },
  saveLinkResolver: {
    type: Object as PropType<ApiLinkResolver>,
    required: false
  },
  reloadAfterSave: {
    type: Boolean,
    required: false,
    default: false
  }
})

const formKey = ref(0)
const errorState = ref(false)

const i18n = I18N.of("HarmonyViewModelForm")

const reloadForm = async () => {
  await props.viewModelContext.hardFetchApiState()
  formKey.value = formKey.value + 1 // this forces the form to reload
}

if(props.loadLinkResolver) {
  props.viewModelContext.getContextConfig().loadLinkResolver = props.loadLinkResolver
}
if(props.saveLinkResolver) {
  props.viewModelContext.getContextConfig().saveLinkResolver = props.saveLinkResolver
}
props.viewModelContext.getContextConfig().afterSaveListeners.push({
  async afterSave(response: HarmonyAxiosResponse) {
    if(response.success && props.reloadAfterSave) {
      await reloadForm()
    }
  }
})

if(process.client) {
  try {
    if(!props.viewModelContext.hasLocalStateInStore())
      await props.viewModelContext.hardFetchApiState()
  }catch (e) {
    errorState.value = true
  }
}

onUnmounted(() => {
  props.viewModelContext.clearListener()
})

defineExpose({
  reloadForm
})

</script>

<style scoped>

.panel {
  margin-top: 5%;
  margin-left: 5%;
  margin-right: 5%;
}

</style>