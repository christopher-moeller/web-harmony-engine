<template>
  <HarmonyWebViewPageHeader />
  <HarmonyBaseWebLayout>
    <h3>{{i18n.translate('Language').build()}}</h3>
    <LanguageSettings />
  </HarmonyBaseWebLayout>
</template>

<script setup lang="ts">

import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import LanguageSettings from "~/components/view/LanguageSettings.vue";
import HarmonyBaseWebLayout from "~/components/view/web/HarmonyBaseWebLayout.vue";

const i18n = I18N.of("web-language-page")
</script>

<style scoped>

</style>