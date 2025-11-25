<template>
  <harmony-select-field
                        :caption="caption"
                        v-model="value"
                        :rows="rows"
                        :columns="columns"
                        :single-selection="true"
                        :show-preview="true"
                        :preview-item-path="previewItemPath"
  />
</template>

<script lang="ts" setup>
import {ref, watch} from "vue";
import HarmonySelectField from "@core/components/base/HarmonySelectField.vue";
import {PropType} from "vue";
import I18N from "@core/utils/I18N";
import {ApiLink, CrudResourceInfoWithOverview} from "~/CoreApi";
import {HarmonyAxiosResponse, HarmonyTableColumn, TableFilterContext} from "~/utils/HarmonyTypes";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

const props = defineProps({
  filterKey: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
  },
  tableFilterContext: {
    type: Object as PropType<TableFilterContext>,
    required: true
  },
  schemaData: {
    type: Object as PropType<CrudResourceInfoWithOverview>,
    required: true
  }
})

const filterConfig = props.schemaData?.resourceOverviewTypeSchema?.filters?.find(e => e.filterName === props.filterKey)
if(!filterConfig) {
  throw new Error("Filter configuration for key "+props.filterKey+" not found")
}

const initRows = async () => {
  if(filterConfig.apiLinkForOptions) {
    const apiLink:ApiLink = filterConfig.apiLinkForOptions
    const resourceResponse:HarmonyAxiosResponse = await useHarmonyFetch(useHarmonyCookies())
        .fetch(apiLink.link+"", {method: apiLink.requestMethod+""})

    return resourceResponse.data
  } else {
    return filterConfig.options!.map(e => e.value)
  }
}

// @ts-ignore
const rows = ref(await initRows())
const initialValue = props.tableFilterContext.activeFilters.find(e => e.key === props.filterKey)?.value

const createInitialReactiveValue = () => {
  if(initialValue) {
    if(filterConfig.apiLinkForOptions) {
      // @ts-ignore
      const foundEntry = rows.value.find(e => e.value === initialValue)
      if(!foundEntry) {
        throw new Error("No entry for value " + value + " found")
      }
      return [foundEntry];
    } else {
      return Array.isArray(initialValue) ? initialValue : [initialValue]
    }
  } else {
     return []
  }
}

const value = ref(createInitialReactiveValue())


const labelPath = filterConfig.apiLinkForOptions ? "label" : ""
const previewItemPath = filterConfig.apiLinkForOptions ? "label" : undefined


watch(value, v => {
  if(filterConfig.apiLinkForOptions) {
    console.log("Rows: ", rows)
    console.log("Value: ", v)
    props.tableFilterContext.onValueChanged(props.filterKey, v.map(e => e.value))
  } else {
    props.tableFilterContext.onValueChanged(props.filterKey, v)
  }
})

const i18n = I18N.of("SelectFieldFilter")

const columns:HarmonyTableColumn[] = [{
  id: "",
  caption: i18n.translate("Label").build(),
  path: labelPath
}]

</script>

<style scoped>

</style>