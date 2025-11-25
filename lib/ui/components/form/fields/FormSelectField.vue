<template>
  <form-field-wrapper
      :field-context="formFieldContext"
      v-slot="slotProps"
      @fieldLoaded="onFieldLoaded"
      :fields-are-equal-check="fieldsAreEqualCheck"
  >
    <harmony-select-field v-if="isReady"
                          :caption="props.caption"
                          v-model="formFieldContext.fieldComputed.value"
                          :rows="rows"
                          :columns="internalColumns"
                          :single-selection="singleSelection"
                          :errorMessages="slotProps['errorMessages']"
                          :show-preview="showPreview"
                          :preview-item-path="previewItemPath"
                          :active-step="activeStep"
                          :switcher-steps="switcherSteps"
                          @onAddNewClick="onAddNewClick"
                          :add-button-visible="hasAddNewResourceOption"
                          :is-readonly="isReadOnlyField"
                          ref="harmonySelectField"
    >
      <template #add>
        <FormSelectFieldCreateResourceWrapper
            @on-cancel-click="() => activeStep = 'default'"
            :resource-id="addNewResourceId"
            @on-resource-created="onNewResourceCreated"
        >
          <slot name="resource-add-new"></slot>
        </FormSelectFieldCreateResourceWrapper>
      </template>
    </harmony-select-field>
  </form-field-wrapper>
</template>

<script lang="ts" setup>

import HarmonySelectField from "@core/components/base/HarmonySelectField.vue";
import {getCurrentInstance, nextTick, ref} from "vue";
import I18N from "@core/utils/I18N";
import JsonUtils from "~/utils/JsonUtils";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import {
  CustomFormFieldIsEqualCheck,
  HarmonyAxiosResponse,
  HarmonyContentSwitcherType, HarmonyTableColumn,
  RegisteredViewModelField
} from "~/utils/HarmonyTypes";
import {ApiLink, EnumInputSpecification} from "~/CoreApi";
import FormSelectFieldCreateResourceWrapper
  from "~/components/form/fields/utils/FormSelectFieldCreateResourceWrapper.vue";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

function allElementsHaveFieldLabel(elements:any[]): boolean {
  for (let element of elements) {
    if(!JsonUtils.getFieldValueByPath(element, "data.label")) {
      return false;
    }
  }
  return true
}


const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
  },
  singleSelection: {
    type: Boolean,
    required: false,
    default: false
  },
  definedOptions: {
    type: Array,
    required: false
  },
  addNewResourceId: {
    type: String,
    required: false
  },
  customLabelCaption: {
    type: String,
    required: false
  }
})

const i18n = I18N.of("FormSelectField")

const isReady = ref(false)

const internalLabelCaption = props.customLabelCaption ? props.customLabelCaption : i18n.translate('Label').build()

let getMapper:Function | undefined;
let setMapper:Function | undefined;
if(props.singleSelection) {
  getMapper = (v:any) => v == null ? [] : [v]
  setMapper = (v:any[]) => v.length == 0 ? null : v[0]
} else {
  getMapper = (v:any) => v == null ? [] : v
  setMapper = undefined
}

const fieldsAreEqualCheck:CustomFormFieldIsEqualCheck = {
  isEqual(field1: any, field2: any): boolean {
    const pk1 = field1?.primaryKey
    const pk2 = field2?.primaryKey
    if(!!pk1 && !!pk2) {
      return pk1 == pk2
    } else {
      return JSON.stringify(field1) == JSON.stringify(field2)
    }
  }
}

const formFieldContext = useFormFieldContext(getCurrentInstance(), getMapper, setMapper)


const fieldSchema = formFieldContext.fieldSchema
const inputSpecification = fieldSchema.validInputSpecification!

const internalColumns:HarmonyTableColumn[] = []

const rows = ref<any[]>([])
const previewItemPath = ref("")
const showPreview = ref(allElementsHaveFieldLabel(formFieldContext.fieldComputed.value))
const harmonyFetch = useHarmonyFetch(useHarmonyCookies())

if(props.definedOptions !== undefined) {
  showPreview.value = true
  internalColumns.push({
    id: "",
    path: "",
    caption: internalLabelCaption
  })

  // @ts-ignore
  rows.value = props.definedOptions
} else if (inputSpecification.type === "ApiLinkResourceSpecification") {

  internalColumns.push({
    id: "data.label",
    path: "data.label",
    caption: internalLabelCaption
  })

  // @ts-ignore
  const apiLink:ApiLink = inputSpecification.apiLink
  // @ts-ignore
  rows.value = await nextTick(async () => {
    const response = await harmonyFetch.fetch(apiLink.link! + "", {
      //@ts-ignore
      method: apiLink.requestMethod
    })

    return response.data
  })
  previewItemPath.value = "data.label"


} else if(inputSpecification.type === "EnumInputSpecification") {
  showPreview.value = true
  internalColumns.push({
    id: "",
    path: "",
    caption: internalLabelCaption
  })

  const enumSpecification:EnumInputSpecification = <EnumInputSpecification> inputSpecification
  // @ts-ignore
  rows.value = enumSpecification.values.map(e => e.value)


} else {
  throw new Error("InputSpecification '"+inputSpecification.type+"' is not supported")
}

const isReadOnlyField = ref(true)
const onFieldLoaded = (field: RegisteredViewModelField) => {
  const inputSpecification = field.schema.validInputSpecification
  isReadOnlyField.value = !!inputSpecification!.isReadOnly
}

isReady.value = true

const currentInstance = getCurrentInstance()

const hasAddNewResourceOption = ref(!!currentInstance?.slots['resource-add-new'])
if(hasAddNewResourceOption.value && !props.addNewResourceId) {
  throw new Error("If the option to add new resources is activated (by using the slot) the prop addNewResourceId must not be null")
}
const switcherSteps:HarmonyContentSwitcherType[] = hasAddNewResourceOption ? [{id: "default"}, {id: "add"}] : []
const activeStep = ref("default")

const onAddNewClick = () => {
  activeStep.value = "add"
}

const harmonySelectField = ref()

const onNewResourceCreated = async (response:HarmonyAxiosResponse) => {
  const uuidOfNewItem:string = response.data.data.primaryKey
  // @ts-ignore
  const apiLink:ApiLink = fieldSchema.validInputSpecification.apiLink
  const loadResponse = await harmonyFetch.fetch(apiLink.link!+"", {
    //@ts-ignore
    method: apiLink.requestMethod
  })
  rows.value = loadResponse.data

  const foundValue = rows.value.find(v => v.primaryKey === uuidOfNewItem)
  harmonySelectField.value.addItemToSelectList(foundValue)
  activeStep.value = "default"
}

</script>

<style scoped>

</style>