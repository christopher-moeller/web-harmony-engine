<template>
  <HarmonyAuthenticatedPageHeader>
    <template #subHeader>
      <GenericBreadcrumbNavigation />
    </template>
  </HarmonyAuthenticatedPageHeader>
  <div v-if="versions" id="root">
    <HarmonyPanel caption="Project" class="version-panel">
      <p><b>Frontend: </b> Version: {{versions.frontendProjectVersion.version}} Build: {{versions.frontendProjectVersion.build}}</p>
      <p><b>Backend: </b> Version: {{versions.backendProjectVersion.version}} Build: {{versions.backendProjectVersion.build}}</p>
    </HarmonyPanel>
    <HarmonyPanel caption="Core" class="version-panel">
      <p><b>Frontend: </b> Version: {{versions.frontendCoreVersion.version}} Build: {{versions.frontendCoreVersion.build}}</p>
      <p><b>Backend: </b> Version: {{versions.backendCoreVersion.version}} Build: {{versions.backendCoreVersion.build}}</p>
    </HarmonyPanel>
  </div>
</template>

<script lang="ts" setup>

import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import {VersionDetail} from "@core/CoreApi";
import I18N from "@core/utils/I18N";
import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";
import axios from "axios";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import useCoreApi from "~/composables/useCoreApi";

interface Versions {
  backendCoreVersion:VersionDetail,
  backendProjectVersion:VersionDetail,
  frontendCoreVersion:VersionDetail,
  frontendProjectVersion:VersionDetail
}


const i18n = I18N.of("applicationVersion-index")

const versions = ref<Versions>()

const coreApi = useCoreApi()
const loadVersions = async () => {

  versions.value = {
    backendCoreVersion: (await coreApi.api().getServerInfoApi().getCoreVersion()).data,
    backendProjectVersion: (await coreApi.api().getServerInfoApi().getProjectVersion()).data,
    frontendCoreVersion: (await axios.get("/uicoreversion.json")).data,
    frontendProjectVersion: (await axios.get("/uiprojectversion.json")).data
  }
}

if(process.client) {
  await loadVersions()
}


</script>

<style scoped>

#root {
  display: flex;
  width: 100%;
  flex-wrap: wrap;
}

.version-panel {
  width: 25em;
  margin: 0.5em;
}

</style>