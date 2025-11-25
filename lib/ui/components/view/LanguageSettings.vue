<template>
  <p>{{i18n.translate('Please select your preferred language').build()}}</p>
  <harmony-select-field
      :caption="i18n.translate('Language').build()"
      v-model="currentLanguage"
      :rows="availableLanguages"
      :columns="columns"
      :single-selection="true"
      previewItemPath="label"
      :show-preview="true"
  />
</template>

<script setup lang="ts">

import I18N from "~/utils/I18N";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {CookieNames, HarmonyTableColumn} from "~/utils/HarmonyTypes";
import {ref} from "vue";
import useRouterUtils from "~/composables/useRouterUtils";
import HarmonySelectField from "~/components/base/HarmonySelectField.vue";
import {LanguageInfo} from "~/CoreApi";

const i18n = I18N.of("LanguageSettings")

const harmonyCookies = useHarmonyCookies()
const languageCookieValue = useHarmonyCookies().getCookieValue(CookieNames.LANGUAGE)

const coreApi = useCoreApi()
const response = await coreApi.api().getServerInfoApi().getLanguages()
const availableLanguages:LanguageInfo[] = response.data

const currentLanguage = ref([availableLanguages.find(l => l.id === languageCookieValue)!])
const columns:HarmonyTableColumn[] = [{
  id: "",
  caption: i18n.translate("Label").build(),
  path: "label"
}]

watch(currentLanguage, newSelection => {
  const item:LanguageInfo = newSelection[0]
  setNewLanguage(item.id+"")
})

const routerUtils = useRouterUtils()
const setNewLanguage = (id: string) => {
  harmonyCookies.setCookieValue(CookieNames.LANGUAGE, id)
  routerUtils.reloadCurrentPage()
}

</script>

<style scoped>

</style>