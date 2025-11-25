<template>
  <HarmonyTextField
      v-model="textFieldValue"
      :caption="caption"
      inner-icon="bx-calendar"
      :is-readonly="isReadonly"
      @click="onTextFieldClick"
      :error-messages="errorMessages"
  />

  <HarmonyModal v-model="calendarVisible">
    <div style="display: flex; justify-content: space-between">
      <HarmonyButton :caption="i18n.translate('Clear').build()" @click="onClearClick"/>
      <div class="button-group">
        <HarmonyButton class="btn" :caption="i18n.translate('Cancel').build()" :type="HarmonyButtonType.SECONDARY" @click="() => calendarVisible = false"/>
        <HarmonyButton class="btn" :caption="i18n.translate('Apply').build()" @click="onApplyClick"/>
      </div>
    </div>


    <div class="calendar-header">
      <button @click="prevMonth">&lt;</button>
      <span>{{ monthNames[currentMonth] }} {{ currentYear }}</span>
      <button @click="nextMonth">&gt;</button>
    </div>
    <div class="calendar-grid">
      <span v-for="day in dayNames" class="calendar-day-name">{{ day }}</span>
      <span
          v-for="blank in firstDayOfMonth"
          class="calendar-day-blank"
      ></span>
      <span
          v-for="date in daysInMonth"
          :class="{'calendar-day': true, 'selected': isSelected(date)}"
          @click="onDaySelected(date)"
      >
            {{ date }}
          </span>
    </div>
  </HarmonyModal>
</template>

<script lang="ts" setup>
import {ref, computed} from 'vue';
import HarmonyModal from "~/components/base/HarmonyModal.vue";
import HarmonyTextField from "~/components/base/HarmonyTextField.vue";
import {DateUtils} from "~/utils/DateUtils";
import useLanguageUtils from "~/composables/useLanguageUtils";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import {ComputedUtils} from "~/utils/ComputedUtils";

const i18n = I18N.of("HarmonyDatePicker")

const props = defineProps({
  caption: {
    type: String,
    required: true
  },
  modelValue: {
    type: String,
    required: false
  },
  errorMessages: {
    type: Array,
    required: false
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()
const languageUtils = useLanguageUtils()

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)
const currentStandardDateForCalendar = ref<string | undefined>(props.modelValue ? props.modelValue : undefined)
const initialTextFieldValue = computedModel.value ? await languageUtils.formatDateString(computedModel.value) : undefined
const textFieldValue = ref(initialTextFieldValue)

watch(computedModel, async (value) => {
  currentStandardDateForCalendar.value = value
  textFieldValue.value = value ? await languageUtils.formatDateString(value) : undefined
})

const jsDate = currentStandardDateForCalendar.value ? DateUtils.parseDateStringToDate(currentStandardDateForCalendar.value) : undefined

const calendarVisible = ref(false);

const currentMonth = ref(jsDate ? jsDate.getMonth() - 1 : DateUtils.getCurrentDate().getMonth());
const currentYear = ref(jsDate ? jsDate.getFullYear() : DateUtils.getCurrentDate().getFullYear());

const dayNames = [
  i18n.translate('Mon').build(),
  i18n.translate('Tue').build(),
  i18n.translate('Wed').build(),
  i18n.translate('Thu').build(),
  i18n.translate('Fri').build(),
  i18n.translate('Sat').build(),
  i18n.translate('Sun').build()
]

const monthNames = [
  i18n.translate('January').build(),
  i18n.translate('February').build(),
  i18n.translate('March').build(),
  i18n.translate('April').build(),
  i18n.translate('May').build(),
  i18n.translate('June').build(),
  i18n.translate('July').build(),
  i18n.translate('August').build(),
  i18n.translate('September').build(),
  i18n.translate('October').build(),
  i18n.translate('November').build(),
  i18n.translate('December').build()
]


const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value + 1, 0).getDate();
});

const firstDayOfMonth = computed(() => {
  const firstDay = new Date(currentYear.value, currentMonth.value, 1).getDay();
  return (firstDay === 0 ? 6 : firstDay - 1);
});

const onDaySelected = (day:number) => {
  currentStandardDateForCalendar.value = DateUtils.parseDateToString(new Date(currentYear.value, currentMonth.value, day))
}

const isSelected = (day:number) => {
  return currentStandardDateForCalendar.value === DateUtils.parseDateToString(new Date(currentYear.value, currentMonth.value, day));
};

const onApplyClick = async () => {
  computedModel.value = currentStandardDateForCalendar.value
  calendarVisible.value = false;
}

const onClearClick = () => {
  computedModel.value = undefined;
  calendarVisible.value = false;
}

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11;
    currentYear.value--;
  } else {
    currentMonth.value--;
  }
};

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0;
    currentYear.value++;
  } else {
    currentMonth.value++;
  }
};

const onTextFieldClick = () => {
  calendarVisible.value = true
}

</script>

<style scoped>


input {
  padding: 8px;
  width: 200px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #ccc;
  margin-bottom: 10px;
}

.calendar-header button {
  background-color: transparent;
  border: none;
  cursor: pointer;
  font-size: 18px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.calendar-day-name,
.calendar-day,
.calendar-day-blank {
  text-align: center;
  padding: 8px;
}

.calendar-day {
  cursor: pointer;
}

.calendar-day.selected {
  background-color: var(--harmony-primary);
  color: var(--harmony-light-1);
  border-radius: 50px;
}

.button-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap-reverse;
  justify-content: right;
  margin-bottom: 2em;
}

.btn {
  margin-bottom: 0.5em;
  margin-left: 0.5em;
}


</style>
