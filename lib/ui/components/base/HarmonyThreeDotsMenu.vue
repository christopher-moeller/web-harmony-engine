<template>
  <div class="three-dots-menu dropdown">
    <i class='bx bx-dots-vertical-rounded'></i>
    <div class="dropdown-content">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>

</script>

<style scoped>

.three-dots-menu {
  margin-left: 10px
}

.three-dots-menu i {
  margin-top: 7px;
  cursor: pointer;
}

.dropdown {
  position: relative;
  display: inline-block;
  padding: 20px;
}

.dropdown-content {
  display: none;
  position: absolute;
  right: 0;
  top: 70px;
  background-color: var(--harmony-light-1);
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  z-index: 10000;
  border-radius: 10px;
}

.dropdown:hover .dropdown-content {
  display: block;
}

</style>