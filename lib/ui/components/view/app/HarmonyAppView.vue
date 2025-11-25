<template>
  <HarmonyAppViewSidebar :is-open="isSidebarVisible"/>
  <div class="content">
    <HarmonyAppViewHeader @onMenuBtnClick="onToggleSidebar"/>
    <main :style="isMounted && layoutState.windowSize.value.state === 'MOBILE' ? 'padding: 18px 12px;' : ''">
      <NuxtPage/>
    </main>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import useLayoutState from "~/composables/useLayoutState";
import HarmonyAppViewHeader from "~/components/view/app/HarmonyAppViewHeader.vue";
import HarmonyAppViewSidebar from "~/components/view/app/HarmonyAppViewSidebar.vue";

const layoutState = useLayoutState()
const isSidebarVisible = ref(false)

const openSidebar = () => isSidebarVisible.value = true
const closeSidebar = () => isSidebarVisible.value = false

const onToggleSidebar = () => {
  if(isSidebarVisible.value) {
    closeSidebar()
  } else {
    openSidebar()
  }
}

const screenWidth = computed(() => layoutState.windowSize.value.width)
watch(screenWidth, (width) => {
  if(width < 768) {
    closeSidebar()
  } else {
    openSidebar()
  }
})

const isMounted = ref(false)
onMounted(() => {
  isMounted.value = true
  if(layoutState.windowSize.value.state === "PC") {
    openSidebar()
  }
})


</script>

<style scoped>

.content{
  position: relative;
  width: calc(100% - 230px);
  left: 230px;
  transition: all 0.3s ease;
  overflow-y: hidden;
  top: 0;
  bottom: 0;
}

main {
  width: 100%;
  padding: 1em;
  max-height: calc(100vh - 56px);
  overflow: auto;
  height: 100vh;
}

@media screen and (max-width: 768px) {
  .content{
    width: calc(100% - 60px);
    left: 200px;
  }
}

</style>
