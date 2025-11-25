<template>
  <HarmonyWebViewPageHeader />
  <div style="margin: 0 10em">
    <h3>{{i18n.translate('Imprint').build()}}</h3>
    <single-web-content-presenter-by-area area-name="CORE_IMPRINT_PAGE"/>
  </div>
</template>

<script setup lang="ts">
import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import SingleWebContentPresenterByArea from "~/components/webcontent/SingleWebContentPresenterByArea.vue";

const i18n = I18N.of("imprint")
</script>


<style scoped>

</style>