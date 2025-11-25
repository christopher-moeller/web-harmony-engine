<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="email" resource-name="users">
    <form-text-field  field-path="email" :caption="i18n.translate('E-Mail').build()"/>
    <form-text-field  field-path="firstname" :caption="i18n.translate('Firstname').build()"/>
    <form-text-field  field-path="lastname" :caption="i18n.translate('Lastname').build()"/>
    <harmony-collapsible :with-borders="false" :caption="i18n.translate('Access').build()">
      <form-context path="userAccessConfig">
        <form-checkbox field-path="isAdmin" :caption="i18n.translate('Is Admin').build()"/>
        <form-select-field field-path="mainRole" :caption="i18n.translate('Main Role').build()" :singleSelection="true"></form-select-field>
        <form-select-field
            field-path="roles"
            :caption="i18n.translate('Roles').build()"
            add-new-resource-id="userRoles"
        >
          <template #resource-add-new>
            <form-text-field caption="Label" field-path="label" />
            <form-text-field caption="Description" field-path="description" />
          </template>
        </form-select-field>
        <form-select-field field-path="additionalRights" :caption="i18n.translate('Additional Rights').build()"></form-select-field>
      </form-context>
    </harmony-collapsible>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import FormContext from "~/components/form/fields/FormContext.vue";
import HarmonyCollapsible from "~/components/utils/HarmonyCollapsible.vue";


const i18n = I18N.of("users[id]")

</script>

<style scoped>

</style>