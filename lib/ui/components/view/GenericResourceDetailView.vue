<template>
  <HarmonyAuthenticatedPageHeader>
    <template v-if="showHeaderActions" v-slot:actionArea>
      <SaveAndRevertButton v-if="hasSaveLink" :is-dirty="isDirty" :is-currently-saving="isSaving" @onSave="saveResource" @onRevertChanges="onRevertChanges"/>
      <harmony-three-dots-menu v-if="hasAnyThreeDotsMenuAction">
        <harmony-three-dots-menu-item v-if="hasDeleteLink" :caption="i18n.translate('Delete').build()" @onItemClick="onOpenDeleteModal"/>
        <slot name="additionalMenuItems"></slot>
      </harmony-three-dots-menu>
    </template>
    <template v-slot:subHeader>
      <slot name="subHeader">
        <BreadcrumbNavigation :items="breadcrumbItems" />
      </slot>
    </template>
  </HarmonyAuthenticatedPageHeader>
  <HarmonyPanel>
    <HarmonyResourceForm
        :resource-context="internalResourceContext"
    >
      <slot></slot>
    </HarmonyResourceForm>
  </HarmonyPanel>
  <harmony-yes-no-modal
      v-model="deleteModalVisible"
      :message="i18n.translate('Do you really want to delete this item?').build()"
      @onYes="deleteItem"
  />
</template>

<script lang="ts" setup>
import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import JsonUtils from "~/utils/JsonUtils";
import {computed, PropType, ref} from "vue";
import HarmonyThreeDotsMenu from "~/components/base/HarmonyThreeDotsMenu.vue";
import HarmonyThreeDotsMenuItem from "~/components/base/HarmonyThreeDotsMenuItem.vue";
import HarmonyYesNoModal from "~/components/base/HarmonyYesNoModal.vue";
import I18N from "~/utils/I18N";
import SaveAndRevertButton from "~/components/utils/SaveAndRevertButton.vue";
import useNavigationTree from "~/composables/useNavigationTree";
import {CrudResourceInfoDto, NavigationItemJson} from "~/CoreApi";
import BreadcrumbNavigation from "~/components/utils/BreadcrumbNavigation.vue";
import HarmonyResourceForm from "~/components/form/resource/HarmonyResourceForm.vue";
import useResourceContext, {ResourceContext} from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useBannerContext from "~/composables/useBannerContext";
import {BreadcrumbItem, HarmonyAxiosResponse} from "~/utils/HarmonyTypes";

const props = defineProps({
  breadcrumbLabelVMAttribute: {
    type: String,
    required: false
  },
  resourceName: {
    type: String,
    required: true
  },
  showHeaderActions: {
    type: Boolean,
    required: false,
    default: true
  },
  resourceContext: {
    type: Object as PropType<ResourceContext>,
    required: false
  }
})

const i18n = I18N.of("GenericResourceDetailView")

const emit = defineEmits<{
  (e: 'onResourceDataLoaded', resourceData: any): void,
}>()


const routerUtils = useRouterUtils()

const internalResourceContext = props.resourceContext ? props.resourceContext : useResourceContext(props.resourceName, useRouterUtils().getResourceIdByUrl())
const isDirty = internalResourceContext.getReactiveDirtyState()
const isSaving = internalResourceContext.getReactiveIsSavingState()

const hasDeleteLink = ref(false)
const hasSaveLink = ref(false)

if(internalResourceContext.hasLocalStateInStore()) {
  const state = internalResourceContext.getLocalState()
  hasDeleteLink.value = !!state.schema.deleteLink
  hasSaveLink.value = internalResourceContext.isNewResource() ? !!state.schema.createNewLink : !!state.schema.updateLink
}

internalResourceContext.getContextConfig().schemaLoadedListeners.push({
  async schemaLoaded(resourceSchema: CrudResourceInfoDto): Promise<void> {
    hasDeleteLink.value = !!resourceSchema.deleteLink
    hasSaveLink.value = internalResourceContext.isNewResource() ? !!resourceSchema.createNewLink : !!resourceSchema.updateLink
  }
})

const bannerContext = useBannerContext()
internalResourceContext.getContextConfig().afterSaveListeners.push({
  async afterSave(response: HarmonyAxiosResponse, isNew: boolean): Promise<void> {
    if(!response.success)
      return

    if(isNew) {
      const newId = response.data.data.primaryKey
      await routerUtils.softNavigateToPath(pathWithoutId + "/" + newId)
      bannerContext.pushShortSuccessBanner(i18n.translate("Successfully created").build())
    } else {
      bannerContext.pushShortSuccessBanner(i18n.translate("Successfully updated").build())
    }
  }
})

const onRevertChanges = () => {
  internalResourceContext.revertChanges()
}

const currentPath = routerUtils.getCurrentPath()
const pathWithoutId = currentPath.replace("/"+internalResourceContext.getResourceId(), "")

const navigationTree = useNavigationTree()
const navigationItemPath:NavigationItemJson[] = navigationTree.findTreeItemPathByPageId(<string> routerUtils.getCurrentRoute().value.name)!
const breadcrumbItems = ref<BreadcrumbItem[]>(navigationItemPath ? navigationTree.buildGenericBreadcrumbItemsByPath(navigationItemPath) : [])
internalResourceContext.getContextConfig().dataLoadedListeners.push({
  async dataLoaded(data: any, isNew: boolean): Promise<void> {
    const label = isNew ? i18n.translate('New').build() : (props.breadcrumbLabelVMAttribute ?  JsonUtils.getFieldValueByPath(data, props.breadcrumbLabelVMAttribute) : data.id)

    if(breadcrumbItems.value.length > 0) {
      const lastBreadcrumbItem = breadcrumbItems.value[breadcrumbItems.value.length - 1]
      lastBreadcrumbItem.label = label
      lastBreadcrumbItem.link = currentPath
    }
    emit('onResourceDataLoaded', data)
  }
})

const slots = useSlots()
const hasAdditionalMenuItems = ref(!!slots.additionalMenuItems)

const hasAnyThreeDotsMenuAction = computed(() => (hasDeleteLink.value && !internalResourceContext.isNewResource()) || hasAdditionalMenuItems.value)


const saveResource = async () => {
  await internalResourceContext.saveToApi()
}

const deleteModalVisible = ref(false)

const onOpenDeleteModal = () => {
  deleteModalVisible.value = true
}

const deleteItem = async () => {
  const response = await internalResourceContext.deleteResource()
  if(response.success) {
    await routerUtils.softNavigateToPath(pathWithoutId)
    bannerContext.pushShortSuccessBanner(i18n.translate("Successfully deleted").build())
  }
}

const getResourceId = () => props.resourceContext?.getResourceId()

const getResourceContext = ():ResourceContext => internalResourceContext

defineExpose({
  getResourceId,
  getResourceContext
})


</script>

<style scoped>


</style>