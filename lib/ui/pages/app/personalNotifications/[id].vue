<template>
  <GenericResourceDetailView
      :show-header-actions="false"
      resource-name="personalNotifications"
      @on-resource-data-loaded="onResourceDataLoaded"
      :resource-context="resourceContext"
  >
    <template #subHeader>
      <BreadcrumbNavigation :items="breadcrumbItems" />
    </template>
    <template #default>
      <div>
        <h3 style="margin-bottom: 1em">{{caption}}</h3>
        <p>{{textMessage}}</p>
      </div>
    </template>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import {ActorNotificationDto} from "@core/CoreApi";
import {useNotificationStore} from "@core/store/notificationStore";
import BreadcrumbNavigation from "~/components/utils/BreadcrumbNavigation.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {BreadcrumbItem} from "~/utils/HarmonyTypes";

const i18n = I18N.of("personalNotifications[id]")

const notificationStore = useNotificationStore()

const dataIsLoaded = ref(false)


const breadcrumbItems = ref<BreadcrumbItem[]>([])

const resourceContext = useResourceContext("personalNotifications", useRouterUtils().getResourceIdByUrl())

const coreApi = useCoreApi()
const cookieResolver = useHarmonyCookies()
const onResourceDataLoaded = async (data: ActorNotificationDto) => {
  const id = data.id;
  breadcrumbItems.value =  [{link: "/app/personalNotifications", label: "Notifications"}, {link: "/app/personalNotifications/"+id, label: data.caption+""}]
  await coreApi.api().getPersonalNotificationsApi().changeReadStateOfPersonalNotification(id!, true)
  await notificationStore.init(cookieResolver)
  dataIsLoaded.value = true
}
const caption = resourceContext.getFieldValueRef("caption")
const textMessage = resourceContext.getFieldValueRef("textMessage")

</script>

<style scoped>


</style>