<template>
  <nav>
    <i class='bx bx-menu' @click="onMenuBtnClick"></i>
    <div>
      <h3 class="title">{{title}}</h3>
    </div>
    <NuxtLink :to="{ name: ECoreRouterPage.APP_PERSONALNOTIFICATIONS }" class="notif">
      <i class='bx bx-bell'></i>
      <span v-if="notificationCount > 0" class="count">{{notificationCount}}</span>
    </NuxtLink>
    <a class="profile" @click="profileDropdownIsVisible = !profileDropdownIsVisible">
      <img class="profile-img" v-if="hasProfileImg" src="" :alt="userAbbreviation">
      <div class="profile-div" v-else>
        <client-only>
          <p class="profile-p">{{userAbbreviation}}</p>
        </client-only>
      </div>
    </a>
    <div v-show="profileDropdownIsVisible" class="dropdown-content">
      <ul class="dropdown-menu">
        <li>
          <NuxtLink :to="{ name: ECoreRouterPage.APP_ACCOUNT }" class="dropdown-menu-link"><i class='bx bx-user-circle'></i>{{i18n.translate('Account').build()}}</NuxtLink>
        </li>
        <li>
          <NuxtLink :to="{ name: ECoreRouterPage.APP_LANGUAGE }" class="dropdown-menu-link"><i class='bx bx-world' ></i>{{i18n.translate('Language').build()}}</NuxtLink>
        </li>
        <li>
          <a class="logout-link dropdown-menu-link" @click="onLogoutClick">
            <i class='bx bx-log-out-circle'></i>
            {{i18n.translate('Logout').build()}}
          </a>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue';
import I18N from "~/utils/I18N";
import {useNotificationStore} from "~/store/notificationStore";
import {ECoreRouterPage} from "~/CoreApi";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import userRouterUtils from "~/composables/useRouterUtils";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

const emit = defineEmits<{
  (e: 'onMenuBtnClick'): void
}>()

const routerUtils = userRouterUtils()
const title = computed(() => routerUtils.getTitleOfCurrentPage())
const i18n = I18N.of("HarmonyAuthenticatedNavbar")

const onMenuBtnClick = () => emit('onMenuBtnClick')


const hasProfileImg = ref(false)

const profileDropdownIsVisible = ref(false)
onMounted(() => {
  window.onclick = function(e) {
    // @ts-ignore
    if (!e.target.matches('.dropdown-content') && !e.target.matches('.profile') && !e.target.matches('.profile-img') && !e.target.matches('.profile-div') && !e.target.matches('.profile-p') && !e.target.matches('.dropdown-menu')) {
      profileDropdownIsVisible.value = false
    }
  }
})
const onLogoutClick = async () => {
  await useAuthenticationContext().logoutAndClear()
  await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
}

const user = await useAuthenticationContext().getUser()
const userAbbreviation = computed(() => user.firstname!.charAt(0).toUpperCase() + user.lastname!.charAt(0).toUpperCase())

const notificationStore = useNotificationStore()
notificationStore.init(useHarmonyCookies())
const notificationCount = computed(() => notificationStore.getNotificationInfo.totalUnread)
</script>

<style scoped>
nav {
    height: 56px;
    background: var(--harmony-light-1);
    padding: 0 24px 0 0;
    display: flex;
    align-items: center;
    grid-gap: 24px;
    position: sticky;
    top: 0;
    left: 0;
    z-index: 1000;
}

nav::before{
    content: "";
    position: absolute;
    width: 40px;
    height: 40px;
    bottom: -40px;
    left: 0;
    border-radius: 50%;
    box-shadow: -20px -20px 0 var(--harmony-light-1);
}

nav a {
    color: var(--harmony-black);
}

nav .bx.bx-menu{
    cursor: pointer;
    color: var(--harmony-black);
}

nav div{
    max-width: 400px;
    width: 100%;
    margin-right: auto;
}

nav .notif{
    font-size: 20px;
    position: relative;
}

nav .notif .count{
    position: absolute;
    top: -6px;
    right: -6px;
    width: 20px;
    height: 20px;
    background: var(--harmony-danger);
    border-radius: 50%;
    color: var(--harmony-light-1);
    border: 2px solid var(--harmony-light-1);
    font-weight: 700;
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.profile {
  cursor: pointer;
}

nav .profile img{
    width: 36px;
    height: 36px;
    object-fit: cover;
    border-radius: 50%;
}

nav .profile div {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: var(--harmony-light-3);
  display: flex;
  align-items: center;
  justify-content: center;
}

nav .profile div p {
  font-size: 15px;
  color: var(--harmony-light-1);
}

nav .theme-toggle{
    display: block;
    min-width: 50px;
    height: 25px;
    background: var(--harmony-light-2);
    cursor: pointer;
    position: relative;
    border-radius: 25px;
}

nav .theme-toggle::before{
    content: "";
    position: absolute;
    top: 2px;
    left: 2px;
    bottom: 2px;
    width: calc(25px - 4px);
    background: var(--harmony-primary);
    border-radius: 50%;
    transition: all 0.3s ease;
}

nav #theme-toggle:checked+.theme-toggle::before{
    left: calc(100% - (25px - 4px) - 2px);
}

.title {
  word-break: break-word;
  color: var(--harmony-black);
}

@media screen and (max-width: 576px) {

  form.show~.notif, form.show~.profile{
    display: none;
  }

}

.dropdown-content {
  display: block;
  position: absolute;
  right: 0;
  top: 50px;
  background-color: var(--harmony-light-1);
  width: 200px;
  z-index: 1;
  border-radius: 5px;
  padding-top: 20px;
  padding-bottom: 20px;
}

.dropdown-menu li {
  background: transparent;
  margin-left: 20px;
  border-radius: 48px 0 0 48px;
  padding: 15px 4px 4px;
}


.dropdown-menu-link {
  width: 100%;
  height: 100%;
  background: var(--harmony-light-1);
  display: flex;
  align-items: center;
  border-radius: 48px;
  font-size: 16px;
  color: var(--harmony-black);
  white-space: nowrap;
  transition: all 0.3s ease;
}

.logout-link {
  color: var(--harmony-danger);
  cursor: pointer;
  margin-top: 30px
}

.dropdown-menu-link i {
  margin-right: 20px;
}
</style>