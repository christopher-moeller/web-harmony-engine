<template>
  <header>
    <HarmonyViewLogo
        :is-in-app-mode="false"
        :name="name"
    />
    <div class="action-area">
      <slot name="actionArea">
        <i
            class='bx bxs-grid bx-md grid-icon'
            style="margin-top: 0.25em; margin-right: 0.5em"
            @click="userModalIsVisible = !userModalIsVisible"
        />
      </slot>
    </div>
  </header>
  <harmony-user-web-modal v-model="userModalIsVisible" />
</template>

<script setup lang="ts">
import HarmonyViewLogo from "~/components/view/HarmonyViewLogo.vue";
import HarmonyUserWebModal from "~/components/view/web/HarmonyUserWebModal.vue";
import useProjectMetaData from "~/composables/useProjectMetaData";

defineProps({
  isRegisterButtonVisible: {
    type: Boolean,
    required: false,
    default: true
  },
  isLoginButtonVisible: {
    type: Boolean,
    required: false,
    default: true
  }
})

const projectMetaData = useProjectMetaData()

const name = projectMetaData.projectLongName

const userModalIsVisible = ref(false)

</script>

<style scoped>

header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.action-area {
  margin-right: 0.5em;
}


.grid-icon {
  color: var(--harmony-light-4);
  cursor: pointer;
}

</style>