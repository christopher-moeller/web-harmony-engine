<template>
  <StarterWebViewPageHeader />
  <HarmonyBaseWebLayout>
    <div id="harmony-slogan-area">
      <div>
        <p class="harmony-slogan"><span>Starter.</span> The place for your</p>
        <p class="harmony-slogan">projects</p>
      </div>
    </div>
    <div class="create-area">
      <div>
        <create-project-form />
      </div>
    </div>
  </HarmonyBaseWebLayout>

</template>

<script setup lang="ts">


import HarmonyBaseWebLayout from "@core/components/view/web/HarmonyBaseWebLayout.vue";
import StarterWebViewPageHeader from "~/components/view/app/StarterWebViewPageHeader.vue";
import CreateProjectForm from "~/components/starter/CreateProjectForm.vue";



</script>

<style scoped>

#harmony-slogan-area {
  display: flex;
  justify-content: center;
  margin-top: 5em;
}

.harmony-slogan {
  font-size: 30px;
  color: grey;
}

.harmony-slogan span{
  color: black;
}

.create-area {
  display: flex;
  justify-content: center;
  margin-top: 5em;
}

</style>