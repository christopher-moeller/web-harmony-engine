<template>
  <div class="container">
    <HarmonyAuthenticatedPageHeader>
      <template v-slot:actionArea>
        <slot name="actionArea"></slot>
        <div class="action-btn-row" v-if="hasAddAction">
          <harmony-button :caption="i18n.translate('Add').build()" @click="onAddNewClick"/>
        </div>
        <harmony-three-dots-menu v-if="hasMenuItemsSlot">
          <slot name="menuItems"></slot>
        </harmony-three-dots-menu>
      </template>
      <template v-slot:subHeader>
        <GenericBreadcrumbNavigation />
      </template>
    </HarmonyAuthenticatedPageHeader>
    <slot name="beforeTable"></slot>
    <HarmonyLazyLoadingTable
        v-if="proposedTableFormat"
        ref="table"
        :columns="internalColumns"
        :onOpenEntry="onOpenEntry"
        :api-link="getAllLink"
        :has-filter="hasFilterSlot"
        :proposedTableFormat="proposedTableFormat"
        @onFiltersChanged="onFiltersChanged"
        @onSortingChanged="onSortingChanged"
        :initial-sort-by-column="internalInitialSortByColumn"
        :initial-filters="storedFilters"
    >
      <template #filter="{filterContext}">
        <slot name="filter" :filterContext="filterContext" :schemaData="schemaData"></slot>
      </template>

      <template v-for="name in customHeaders" v-slot:[name]>
        <slot :name="name" />
      </template>

      <template v-for="name in customColumns" v-slot:[name]="{ row }" >
        <slot :name="name" :row="row"/>
      </template>

      <template v-slot:column="{cell, row}" >
        <slot name="column" :cell="cell" :row="row"/>
      </template>

    </HarmonyLazyLoadingTable>
  </div>
</template>

<script lang="ts" setup>

import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import {computed, PropType, ref, useSlots} from "vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import HarmonyLazyLoadingTable from "~/components/base/HarmonyLazyLoadingTable.vue";
import HarmonyThreeDotsMenu from "~/components/base/HarmonyThreeDotsMenu.vue";
import I18N from "~/utils/I18N";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import {CrudResourceInfoWithOverview, ECoreBackendPath, GeneralApiResource} from "~/CoreApi";
import useRouterUtils from "~/composables/useRouterUtils";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {useCustomFilterStore} from "~/store/customFilterStore";
import {CustomFilterConfig, CustomResourceConfig, HarmonyTableColumn} from "~/utils/HarmonyTypes";

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  resourceName: {
    type: String,
    required: true
  },
  customTitle: {
    type: String,
    required: false
  },
  initialSortByColumn: {
    type: String,
    required: false
  }
})

const i18n = I18N.of("GenericResourceOverview")

const routerUtils = useRouterUtils()
const currentPath = routerUtils.getCurrentPath()

const resourceName = props.resourceName!

const harmonyFetch = useHarmonyFetch(useHarmonyCookies())
const response = await harmonyFetch.fetch(ECoreBackendPath.API_APPRESOURCES_RESOURCENAME.replace("{resourceName}", resourceName))
const schemaData: CrudResourceInfoWithOverview = <CrudResourceInfoWithOverview> response.data

//@ts-ignore
const columnIdsBySchema = Object.keys(schemaData.resourceOverviewTypeSchema.fields)

const customFilterStore = useCustomFilterStore()

const configForResource:CustomResourceConfig | undefined = customFilterStore.getResourceTableFilterConfig(resourceName)
const storedFilters:CustomFilterConfig[] = configForResource ? configForResource.filters : []

const internalInitialSortByColumn = ref(configForResource?.sortingParam ? configForResource.sortingParam : props.initialSortByColumn)

const internalColumns:HarmonyTableColumn[] = []
props.columns.forEach(col => {
  if(columnIdsBySchema.includes(col.id)) {
    internalColumns.push({
      id: col.id,
      path: col.path ? col.path : "data." + col.id,
      caption: col.caption,
      isDate: col.isDate,
      sortByKey: col.sortByKey
    })
  }
})

const getAllLink = schemaData.getAllLink


const table = ref()
const fetchCleanNewData = () => table.value.fetchCleanNewData()

defineExpose({
  fetchCleanNewData
})

const onOpenEntry = (row: GeneralApiResource<any>) => {
  routerUtils.softNavigateToPath(currentPath + "/" + row.primaryKey)
}

const hasAddAction = computed(() => !!schemaData.createNewLink)

const onAddNewClick = () => {
  if(!hasAddAction.value)
    throw new Error("It is not allowed to create a new resource")

  routerUtils.softNavigateToPath(currentPath + "/" + "new")
}

const slots = useSlots()
const hasFilterSlot = ref(!!slots.filter)
const hasMenuItemsSlot = ref(!!slots.menuItems)

const onFiltersChanged = (filters:[{key: string, value: string}]) => {

  const filterConfigs:CustomFilterConfig[] = filters.map(e => {return {filterKey: e.key, filterValue: e.value}})

  let resourceEntry:CustomResourceConfig | undefined = customFilterStore.getResourceTableFilterConfig(resourceName)
  if(!resourceEntry) {
    customFilterStore.addResourceTableFilterConfig({resourceName: resourceName!, filters: []})
    resourceEntry = customFilterStore.getResourceTableFilterConfig(resourceName)!
  }
  resourceEntry.filters = filterConfigs
}

const onSortingChanged = (sortingParam?: string) => {

  let resourceEntry:CustomResourceConfig | undefined = customFilterStore.getResourceTableFilterConfig(resourceName)
  if(!resourceEntry) {
    customFilterStore.addResourceTableFilterConfig({resourceName: resourceName!, filters: []})
    resourceEntry = customFilterStore.getResourceTableFilterConfig(resourceName)!
  }

  resourceEntry.sortingParam = sortingParam
}

const proposedTableFormat = ref()
onMounted(() => {
  if(window.innerWidth < 768) {
    proposedTableFormat.value = "CARD"
  } else {
    proposedTableFormat.value = "TABLE"
  }
})

const customHeaders = Object.keys(slots).filter(c => c.startsWith("header:"))
const customColumns = Object.keys(slots).filter(c => c.startsWith("column:"))

</script>

<style scoped>

.container {
  display: flex;
  flex-flow: column;
  height: 100%;
}

</style>