<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="attribute" resource-name="i18nEntityAttribute">
    <template v-slot:additionalMenuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Next').build()" @onItemClick="goToNextUnresolved" :is-in-loading-mode="isCurrentlyRedirecting"/>
    </template>
    <form-text-field field-path="entityClass" :caption="i18n.translate('Entity Class').build()"/>
    <form-text-field field-path="attribute" :caption="i18n.translate('Attribute Name').build()"/>
    <form-nested-array-handler field-path="values" :caption="i18n.translate('Translations').build()">
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
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormNestedArrayHandler from "~/components/form/fields/FormNestedArrayHandler.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("entityAttributes[id]")

const isCurrentlyRedirecting = ref(false)
const routerUtils = useRouterUtils()

const coreApi = useCoreApi()
const goToNextUnresolved = async () => {
  const nextId:string = (await coreApi.api().getI18nEntityAttributeApi().getIdOfNextEntryToBeResolved()).data + ""
  const routerPageName = routerUtils.getCurrentRoute().value.name!.toString();
  await routerUtils.softNavigateToPage(routerPageName, {id: nextId})
}

</script>

<style scoped>


</style>