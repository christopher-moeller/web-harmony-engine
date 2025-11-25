<template>
  <div class="banner-modal-background">
    <template v-for="(banner, index) in reversed" :key="index">
        <div class="banner-flex-wrapper">
          <div class="banner" :class="index === 0 ? cssClassOfTopBanner : []">
            <div v-if="banner.manualClosable" class="close-icon-wrapper">
              <i class='bx bxs-x-circle close-icon' @click="onCloseBannerClick(banner)"></i>
            </div>
            <template v-if="banner.type === BannerType.SUCCESS">
              <SuccessBanner :banner="banner"/>
            </template>
            <template v-else-if="banner.type === BannerType.WARNING">
              <WarningBanner :banner="banner"/>
            </template>
            <template v-else-if="banner.type === BannerType.ERROR">
              <ErrorBanner :banner="banner"/>
            </template>
          </div>
        </div>
    </template>
  </div>
</template>

<script setup lang="ts">

import useBannerContext from "~/composables/useBannerContext";
import {Banner, BannerType} from "~/store/bannerStore";
import SuccessBanner from "~/components/banner/impl/SuccessBanner.vue";
import WarningBanner from "~/components/banner/impl/WarningBanner.vue";
import ErrorBanner from "~/components/banner/impl/ErrorBanner.vue";
import {EApplicationStatus} from "~/CoreApi";
import useCoreApi from "~/composables/useCoreApi";

const bannerContext = useBannerContext()
const activeBanners = bannerContext.getActiveBanners()
const cssClassOfTopBanner = ref()
const countOfVisibleBanners = ref(0)
const reversed = computed(() => [...activeBanners.value].reverse().slice(0,3))


watch(activeBanners, async (newValue) => {
  if(newValue.length > countOfVisibleBanners.value) {
    cssClassOfTopBanner.value = "not-showing"
    setTimeout(() => {
      cssClassOfTopBanner.value = "showing"
    }, 100);
  }

  countOfVisibleBanners.value = activeBanners.value.length
}, {deep: true})
const coreApi = useCoreApi()
const serverStatus = await coreApi.api().getApplicationStatusApi().getCurrentStatus()
if(serverStatus.data.status != EApplicationStatus.OK) {
  bannerContext.pushMaintenanceBanner(serverStatus.data.userMessage+"")
}

const onCloseBannerClick = (banner: Banner) => {
  bannerContext.removeBanner(banner.uuid)
}

</script>

<style scoped>

.banner-modal-background {
  position: fixed;
  z-index: 6000;
  bottom: 2em;
  width: 100%;
}

.banner-flex-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 1em;
}

.banner {
  opacity: 0.9;
  transition: all 1s;
}

.not-showing {
  display: none;
}

.showing {
  opacity: 0.9;
  display: block;
}

@starting-style {
  .showing {
    opacity: 0;
  }
}

.close-icon-wrapper {
  display: flex;
  justify-content: flex-end;
}

.close-icon {
  position: relative;
  top: 15px;
  left: 15px;
  cursor: pointer;
  color: var(--harmony-black);
}

</style>