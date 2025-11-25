<template>
  <HarmonyTable
      ref="harmonyTableRef"
      :columns="columns"
      :rows="rows"
      :is-sortable="true"
      :onOpenEntry="onOpenEntry"
      :proposed-table-format="proposedTableFormat"
      :is-in-loading-mode="isInLoadingMode"
      :initial-sort-by-column-id="sortByColumn"
      :is-initial-sort-asc="isAscSort"
      @onBottomScrolled="onBottomScrolled"
      @onSortingOptionsChanged="onSortingOptionsChanged"
  >
    <template #custom-action-header>
      <div style="display: flex">
        <div>
          <i v-if="hasFilter" class='bx bx-filter-alt filter-icon' @click="isFilterAreaVisible = !isFilterAreaVisible"></i>
        </div>
        <p class="total-result-label">{{i18n.translate('Total:').build()}} {{totalResults}}</p>
      </div>
    </template>
    <template #sub-header>
      <div v-if="isFilterAreaVisible && hasFilter">
        <slot name="filter" :filterContext="filterContext"></slot>
      </div>
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

  </HarmonyTable>
</template>

<script lang="ts" setup>

import {PropType, useSlots} from "vue";
import HarmonyTable from "@core/components/base/table/HarmonyTable.vue"
import {nextTick, onMounted, ref} from "vue";
import I18N from "@core/utils/I18N";
import {ApiLink, GeneralApiResource, SearchResult} from "~/CoreApi";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {CustomFilterConfig, HarmonyTableColumn, TableFilterContext} from "~/utils/HarmonyTypes";

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  onOpenEntry: {
    type: Function,
    required: false
  },
  apiLink: {
    type: Object as PropType<ApiLink>,
    required: true
  },
  hasFilter: {
    type: Boolean,
    required: false,
    default: false
  },
  proposedTableFormat: {
    type: String,
    required: false,
    default: "TABLE"
  },
  initialSortByColumn: {
    type: String,
    required: false
  },
  initialFilters: {
    type: Array as PropType<CustomFilterConfig[]>,
    required: false,
    default: []
  }
})

const i18n = I18N.of("HarmonyLazyLoadingTable")

let currentPage = 0;
let currentSize = 10;
let allDataLoaded = false;

let sortByColumn:string | undefined = props.initialSortByColumn ? props.initialSortByColumn.split(":")[0] : undefined
let isAscSort = props.initialSortByColumn ? (props.initialSortByColumn.includes(":") ? props.initialSortByColumn.split(":")[1] === "asc" : true) : true

const emit = defineEmits<{
  (e: 'onFiltersChanged', filters: {key: string, value: string}[]): void,
  (e: 'onSortingChanged', sortParam?: string): void
}>()

const isInLoadingMode = ref(true)


const filterContext:TableFilterContext = {
  onValueChanged: (filterKey: String, value:any) => {
    const currentEntry = filterContext.activeFilters.find(e => e.key === filterKey)
    const effectiveValue = !!value ? (Array.isArray(value) ? value.join(",") : value) : ""
    if(currentEntry) {
      currentEntry.value = effectiveValue
    } else {
      filterContext.activeFilters.push({key: filterKey+"", value: effectiveValue})
    }
    emit('onFiltersChanged', filterContext.activeFilters)
    fetchCleanNewData()
  },
  activeFilters: props.initialFilters.map(e => {return {key: e.filterKey, value: e.filterValue}})
}


const rows = ref<GeneralApiResource<any>[]>([])

const totalResults = ref<Number>(0)

const fetchNextData = async () => {

  const sortParam = sortByColumn ? sortByColumn + ":" + (isAscSort ? "asc" : "desc") : undefined

  const query = { page: currentPage, size: currentSize, sort: sortParam }
  filterContext.activeFilters.forEach(entry => {
    if(entry.key && entry.value) {
      if(entry.value) {
        // @ts-ignore
        query[entry.key] = entry.value
      }
    }
  })

  const harmonyFetch = useHarmonyFetch(useHarmonyCookies())
  const {data: dataResponse} = await harmonyFetch.fetch(props.apiLink.link!+"", {
    //@ts-ignore
    method: props.apiLink.requestMethod,
    queryParams: query
  })

  const searchResult:SearchResult = <SearchResult> dataResponse
  totalResults.value = searchResult.totalResults!
  rows.value.push(...searchResult.data!)
  allDataLoaded = !!searchResult.totalResults && searchResult.totalResults!.valueOf() <= rows.value.length
  isInLoadingMode.value = false
}

const fetchCleanNewData = async () => {
  isInLoadingMode.value = true
  currentPage = 0;
  currentSize = 20;
  allDataLoaded = false;
  rows.value = []

  await fetchNextData()
}

defineExpose({
  fetchCleanNewData
})

onMounted(() => {
  nextTick(() => {
    fetchCleanNewData()
  })
})


const onBottomScrolled = () => {
  if(!allDataLoaded) {
    currentPage++
    fetchNextData()
  }
}

const onSortingOptionsChanged = (sort: any) => {
  const sortByColumnId = sort.column?.replace("data.", "")

  if(sortByColumnId) {
    const harmonyColumn:HarmonyTableColumn = props.columns?.find(c => c.id === sortByColumnId)!
    if(!harmonyColumn) {
      throw new Error("Column with path " + sort.column + " not found!")
    }
    sortByColumn = harmonyColumn.sortByKey ? harmonyColumn.sortByKey : sortByColumnId
  } else {
    sortByColumn = sortByColumnId
  }


  isAscSort = sort.isAsc

  const sortParam = sortByColumn ? sortByColumn + ":" + (isAscSort ? "asc" : "desc") : undefined
  emit('onSortingChanged', sortParam)
  fetchCleanNewData()
}


const harmonyTableRef = ref()

const isFilterAreaVisible = ref(false)

const slots = useSlots()
const customHeaders = Object.keys(slots).filter(c => c.startsWith("header:"))
const customColumns = Object.keys(slots).filter(c => c.startsWith("column:"))

</script>

<style scoped>

.filter-icon {
  cursor: pointer;
  color: var(--harmony-light-3);
}

.total-result-label {
  color: var(--harmony-light-3);
  margin-left: 10px;
  width: 100%
}

</style>