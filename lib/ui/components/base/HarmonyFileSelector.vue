<template>
  <div id="container">
    <div id="header">
      <p class="caption" :class="!!errorMessages ? ['danger-text'] : []">{{caption}}</p>
      <div>
        <input
            type="file"
            multiple
            name="file"
            id="file-input"
            @change="onFileSelectionChange"
            ref="hiddenInput"
        />
        <label id="file-input-label" for="file-input" class="add-button">
          {{i18n.translate('Add').build()}}
        </label>
      </div>
    </div>
    <div id="preview-container"
         @dragover="dragover"
         @dragleave="dragleave"
         @drop="drop"
         :style="isDragging ? 'background-color: rgba(0,0,0,0.2)' : ''"
         :class="!!errorMessages ? ['danger-element-wrapper'] : []"
    >
      <div v-if="computedModel.length === 0" id="empty-files-container" @click="onEmptyAreaClick">
        <p>{{i18n.translate('Drop files here or click here to upload').build()}}</p>
      </div>
      <div v-for="(file, key) in computedModel" :key="key" @click="onItemClick(file)">
        <div v-if="isImagePreviewType(file)" class="element-wrapper">
          <img :src="file.base64Content" alt="File" class="preview-img">
        </div>
        <div v-else class="element-wrapper">
          <div class="none-img-preview">
            <p>{{file.name}}</p>
          </div>
        </div>
      </div>
    </div>
    <div id="error-messages">
      <p v-if="internalErrorMessage" class="error-message">{{internalErrorMessage}}</p>
      <p class="error-message" style="font-size: 12px;" v-for="(error, index) in errorMessages" :key="index">{{error}}</p>
    </div>
  </div>
  <HarmonyModal v-model="isDetailModalOpen" :caption="selectedWebFile?.name">
    <div v-if="selectedWebFile">
      <p><b>Type</b> {{selectedWebFile.type}}</p>
      <p><b>Size</b> {{(selectedWebFile.size / 1000000)}} MB</p>
      <p><b>Last Modified</b> {{selectedWebFile.lastModified}}</p>
      <div v-if="isImagePreviewType(selectedWebFile)" style="display: flex; justify-content: center; margin-top: 1em">
        <img :src="selectedWebFile.base64Content" alt="File" class="preview-img-modal">
      </div>
      <div class="modal-btn-row" style="margin-top: 5em">
        <HarmonyButton caption="Download" class="modal-btn" @click="onDownloadFile(selectedWebFile)"/>
      </div>
      <div class="modal-btn-row">
        <HarmonyButton caption="Remove" class="modal-btn" @click="onRemoveFile(selectedWebFile)"/>
      </div>
      <div class="modal-btn-row">
        <HarmonyButton caption="Close" class="modal-btn" :type="HarmonyButtonType.SECONDARY" @click="closeModal"/>
      </div>
    </div>
  </HarmonyModal>
</template>

<script lang="ts" setup>

import {FileWebData} from "@core/CoreApi";
import {PropType, ref} from "vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import {UUIDUtils} from "@core/utils/UUIDUtils";
import {DateUtils} from "@core/utils/DateUtils";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";
import useFileUtils from "~/composables/useFileUtils";

const convertInputFileToBinaryString = async (file:any):Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => resolve(<string>reader.result)
    reader.readAsDataURL(file);
    reader.onerror = reject
  })
}

const props = defineProps({
  caption: {
    type: String,
    required: false
  },
  modelValue: {
    type: Array as PropType<FileWebData[]>,
    required: false,
    default: []
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  },
  errorMessages: {
    type: Array,
    required: false
  },
  min: {
    type: Number,
    required: false,
    default: -1
  },
  max: {
    type: Number,
    required: false,
    default: -1
  }
})

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const i18n = I18N.of("HarmonyFileSelector")

const isDragging = ref<boolean>(false)

const selectedWebFile = ref<FileWebData | undefined>()
const isDetailModalOpen = ref(false)
watch(isDetailModalOpen, v => {
  if(v === false) {
    selectedWebFile.value = undefined
  }
})

const hiddenInput = ref()

const internalErrorMessage = ref()
const validateFileAmounts = (amountOfNewFiles: number): boolean => {

  const amountOfCurrentFiles = computedModel.value ? computedModel.value.length : 0;
  const totalAmount = amountOfNewFiles + amountOfCurrentFiles;

  if(props.min > -1 && totalAmount < props.min) {
    internalErrorMessage.value = i18n.translate('You need to select at least {min} file(s)').add('min', props.min).build()
    return false;
  }

  if(props.max > -1 && totalAmount > props.max) {
    internalErrorMessage.value = i18n.translate('You are not allowed to select more than {max} file(s)').add('max', props.max).build()
    return false;
  }

  internalErrorMessage.value = undefined
  return true;
}

const addFiles = async (files: any) => {
  const length = files.length;
  if(!validateFileAmounts(length)) {
    hiddenInput.value.value = '';
    return;
  }

  for(let i=0; i<length; i++) {
    const file = files[i];

    const fileWebData:FileWebData = {}

    fileWebData.id = "local/" + UUIDUtils.createUUID();
    fileWebData.name = file.name;
    fileWebData.size = file.size;
    fileWebData.type = file.type;
    fileWebData.lastModified = DateUtils.parseDateTimeToString(file.lastModifiedDate)
    fileWebData.base64Content = await convertInputFileToBinaryString(file)

    computedModel.value.push(fileWebData);
  }
  hiddenInput.value.value = '';
}
const onFileSelectionChange = async (e:any) => {
  await addFiles(e.target.files)
}

const isImagePreviewType = (file:FileWebData):boolean => {
  if(file.base64Content == undefined)
    return false;

  return file.type === "image/png" || file.type === "image/jpg" || file.type == "image/jpeg"
}

const onItemClick = (file:FileWebData) => {
  selectedWebFile.value = file
  isDetailModalOpen.value = true
}

const closeModal = () => {
  isDetailModalOpen.value = false;
}

const onRemoveFile = (file: FileWebData) => {
  if(!validateFileAmounts(-1)) {
    hiddenInput.value.value = '';
    return;
  }

  const itemToRemove = computedModel.value.find((e:FileWebData) => e.id === file.id);
  if(itemToRemove) {
    const index = computedModel.value.indexOf(itemToRemove)
    computedModel.value.splice(index, 1)
  }
  closeModal();
}

const fileUtils = useFileUtils()

const onDownloadFile = async (file: FileWebData) => {
  if(file.downloadLink) {
    await fileUtils.downloadFileFromApi(file.downloadLink.link+"")
  } else {
    const blob:Blob = fileUtils.createBlobFromBase64EncodedFile(file.base64Content+"")
    fileUtils.saveBlobInBrowserDownloads(blob, file.name+"")
  }
}

const dragover = (e:any) => {
  e.preventDefault();
  isDragging.value = true;
}

const dragleave = () => {
  isDragging.value = false;
}

const drop = async (e:any) => {
  e.preventDefault();
  const droppedFiles = e.dataTransfer.files
  await addFiles(droppedFiles);
  isDragging.value = false;
}

const onEmptyAreaClick = () => {
  hiddenInput.value.click();
}

</script>

<style scoped>

#container {
  margin-bottom: 1em;
}

#header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5em;
}

.caption {
  font-size: 14px;
  margin-top: 2em;
  margin-left: 25px;
  font-weight: 500;
  color: var(--harmony-black);
  width: 100%;
  display: block;
  user-select: none;
}

#file-input {
  display: none;
}

#file-input-label {
  cursor: pointer;
}

.add-button {
  border: none;
  color: var(--harmony-light-1);
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  cursor: pointer;
  border-radius: 100px;
  background-color: var(--harmony-primary);
}

#preview-container {
  display: flex;
  flex-wrap: wrap;
  border-radius: 20px;
  border-color: var(--harmony-light-3);
  border-style: solid;
  border-width: 1px;
  min-height: 120px;
}

.preview-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.preview-img-modal {
  width: 200px;
  height: 200px;
  object-fit: cover;
}

.element-wrapper {
  border-radius: 20px;
  border-color: var(--harmony-light-3);
  border-style: solid;
  border-width: 1px;
  margin: 0.5em;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  width: 100px;
  height: 100px;
}

.none-img-preview {
  width: 100px;
  height: 100px;
  overflow: hidden;
  background-color: white;
  padding: 5px;
}

.none-img-preview p {
  font-size: 10px;
  inline-size: 80px;
  overflow-wrap: break-word;
}

.modal-btn-row {
  margin: 1em;
}

.modal-btn {
  width: 100%;
}

#empty-files-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.danger-text {
  color: var(--harmony-danger);
}

.danger-element-wrapper {
  border-color: var(--harmony-danger) !important;
  color: var(--harmony-danger) !important;
}

.error-message {
  color: var(--harmony-danger);
  font-size: 12px;
  margin-top: 2px;
  margin-left: 25px;
}

</style>