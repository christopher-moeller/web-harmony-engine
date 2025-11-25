<template>
  <i v-if="showDevInfo" class='bx bx-hard-hat field-info-btn' @click="onFieldInfoClick"></i>
  <slot :error-messages="errorMessages"></slot>
  <harmony-modal v-if="showDevInfo" v-model="showFieldInfoModal">
    <h3>{{formFieldContext.fullFieldPath}}</h3>
    <p>{{registeredField}}</p>
    <div class="btn-wrapper">
      <harmony-button :caption="i18n.translate('Close').build()" @click="showFieldInfoModal = false"/>
    </div>
  </harmony-modal>
</template>

<script lang="ts" setup>

import {computed, PropType, ref} from "vue";
import {useDevStore} from "@core/store/devStore";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import I18N from "@core/utils/I18N";
import {FormFieldContext} from "~/composables/form/useFormFieldContext";
import {CustomFormFieldIsEqualCheck, RegisteredViewModelField} from "~/utils/HarmonyTypes";
import {ComplexTypeSchema} from "~/CoreApi";

const props = defineProps({
  fieldContext: {
    type: Object as PropType<FormFieldContext>,
    required: true
  },
  fieldsAreEqualCheck: {
    type: Object as PropType<CustomFormFieldIsEqualCheck>,
    required: false,
    default: {
      isEqual(field1: any, field2: any): boolean {
        let value1 = JSON.stringify(field1)
        let value2 = JSON.stringify(field2)
        if(value1 == undefined || value1 === "null")
          value1 = ""
        if(value2 == undefined || value2 === "null")
          value2 = ""
        return value1 === value2
      }
    }
  }
})

const i18n = I18N.of("FormFieldWrapper")

const emit = defineEmits<{
  (e: 'fieldLoaded', registeredField: RegisteredViewModelField): void
}>()

const formFieldContext:FormFieldContext = props.fieldContext

const originalValue = formFieldContext.originalValue
const registeredField: RegisteredViewModelField = {
  fieldPath: formFieldContext.fullFieldPath,
  originalValue,
  isDirty: !props.fieldsAreEqualCheck.isEqual(originalValue, formFieldContext.currentValue.value),
  schema: <ComplexTypeSchema> formFieldContext.fieldSchema // TODO
}

const regField = formFieldContext.getOrCreateRegisteredField(registeredField)

const isDirty = ref(false)
watch(formFieldContext.currentValue, v => {
  isDirty.value = !props.fieldsAreEqualCheck.isEqual(regField.value.originalValue, v)
  regField.value.isDirty = isDirty.value
}, {deep: true})

const errorMessages = computed(() => regField.value.validationError?.errorMessages!.map(m => m.message))

const devStore = useDevStore()
const showDevInfo = computed(() => devStore.getShowViewModelFieldInfo)

const showFieldInfoModal = ref(false)

const onFieldInfoClick = () => {
  devStore.isDevSidePanelOpen = false
  showFieldInfoModal.value = true
}

emit('fieldLoaded', registeredField)

</script>

<style scoped>

.field-info-btn {
  cursor: pointer;
}

.btn-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>