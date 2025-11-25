<template>
  <div class="header">
    <harmony-button
        :caption="i18n.translate('Cancel').build()"
        :type="HarmonyButtonType.SECONDARY"
        @click="onCancelClick"
    />
    <SaveAndRevertButton
        :is-currently-saving="currentlySaving"
        :is-dirty="isDirty"
        @onRevertChanges="() => resourceContext.revertChanges()"
        @onSave="onSaveClick"
    />
  </div>
  <HarmonyResourceForm :resource-context="resourceContext">
    <slot/>
  </HarmonyResourceForm>
</template>

<script setup lang="ts">
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import {HarmonyAxiosResponse, HarmonyButtonType} from "~/utils/HarmonyTypes";
import SaveAndRevertButton from "~/components/utils/SaveAndRevertButton.vue";
import HarmonyResourceForm from "~/components/form/resource/HarmonyResourceForm.vue";
import useResourceContext from "~/composables/form/useResourceContext";

const i18n = I18N.of("FormSelectFieldCreateResourceWrapper")

const emit = defineEmits<{
  (e: 'onCancelClick'): void,
  (e: 'onResourceCreated', response:HarmonyAxiosResponse): void
}>()

const props = defineProps({
  resourceId: {
    type: String,
    required: true
  }
})

const resourceContext = useResourceContext(props.resourceId, "new")

const currentlySaving = resourceContext.getReactiveIsSavingState()
const isDirty = resourceContext.getReactiveDirtyState()

const onCancelClick = () => emit("onCancelClick")

const onSaveClick = async () => {
  const response = await resourceContext.saveToApi()
  if(response.success) {
    resourceContext.revertChanges()
    emit("onResourceCreated", response)
  }
}

</script>

<style scoped>

.header {
  display: flex;
  justify-content: space-between;
}

</style>