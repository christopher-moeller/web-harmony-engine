<template>
  <HarmonyViewModelForm
      :view-model-context="viewModelContext"
  >
    <form-text-field field-path="projectShortName" caption="Project Name (Short)"/>
    <form-text-field field-path="projectLongName" caption="Project Name (Long)"/>
    <form-text-field field-path="pathToProject" caption="Path to Project Folder"/>
    <form-checkbox field-path="useH2Database" caption="Use H2 Database (not recommended)"/>
    <div class="btn-row">
      <harmony-button caption="Create" class="login-btn" @click="onSubmitClick" :is-in-loading-mode="isCurrentlySending"/>
    </div>
  </HarmonyViewModelForm>
</template>

<script setup lang="ts">

import FormTextField from "@core/components/form/fields/FormTextField.vue";
import HarmonyViewModelForm from "@core/components/form/viewmodel/HarmonyViewModelForm.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import FormCheckbox from "@core/components/form/fields/FormCheckbox.vue";
import useViewModelContext from "@core/composables/form/useViewModelContext";

const viewModelContext = useViewModelContext("CreateHarmonyProjectRequest")

const isCurrentlySending = viewModelContext.getReactiveIsSavingState()

const onSubmitClick = async () => {
  await viewModelContext.saveToApi()
}

</script>

<style scoped>

.btn-row {
  margin-top: 2em;
  display: flex;
  align-items: center;
  justify-content: center;
}

</style>