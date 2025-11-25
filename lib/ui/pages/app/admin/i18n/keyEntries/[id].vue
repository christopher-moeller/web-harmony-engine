<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="key" resource-name="i18nKeyEntries">
    <template v-slot:additionalMenuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Next').build()" @onItemClick="goToNextUnresolved" :is-in-loading-mode="isCurrentlyRedirecting"/>
    </template>
    <form-text-field field-path="classId" :caption="i18n.translate('Class ID').build()"/>
    <form-text-field field-path="key" :caption="i18n.translate('Key').build()"/>
    <form-text-field field-path="placeholders" :caption="i18n.translate('Placeholders').build()"/>
    <form-select-field field-path="codeLocation" :caption="i18n.translate('Code Location').build()" :single-selection="true"/>
    <form-text-field field-path="codeLines" :caption="i18n.translate('Code Lines').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-nested-array-handler field-path="translationEntries" :caption="i18n.translate('Translations').build()">
      <template #default="{element}">
        <form-text-area field-path="translation" :caption="element['language']"/>
      </template>
    </form-nested-array-handler>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import {ref} from "vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormNestedArrayHandler from "~/components/form/fields/FormNestedArrayHandler.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("keyEntries[id]")

const isCurrentlyRedirecting = ref(false)

const routerUtils = useRouterUtils()
const coreApi = useCoreApi()
const goToNextUnresolved = async () => {
  const nextId:string = (await coreApi.api().getI18nKeyEntryApi().getIdOfNextEntryToBeResolved()).data + ""
  const routerPageName = routerUtils.getCurrentRoute().value.name!.toString();
  await routerUtils.softNavigateToPage(routerPageName, {id: nextId})
}

</script>

<style scoped>


</style>