// https://nuxt.com/docs/api/configuration/nuxt-config
import {NuxtPage} from "@nuxt/schema";

import appConfigurationBuilder from "./build/nuxt-app-configuration-builder"
import {FrontendProjectConfiguration, ProjectMetaData, RouterPagesRoot} from "./CoreApi";
import pluginResolver from "./build-plugins";

const coreBuildDirectory = __dirname
const projectBuildDirectory = process.cwd()

const jsonConfig:FrontendProjectConfiguration = appConfigurationBuilder.loadJsonConfig(projectBuildDirectory)
const pagesJsonConfig:RouterPagesRoot = jsonConfig.routerPages!
const projectMetaData:ProjectMetaData = jsonConfig.projectMeta!

export default defineNuxtConfig({
  // @ts-ignore
  css: [
    '@/assets/css/main.css',
  ],
  devtools: { enabled: true },
  app: {
    head: {
      link: [
        {
          rel: "stylesheet",
          href: "https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700;800&display=swap",
        },
        {
          rel: "stylesheet",
          href: "https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css",
        }
      ]
    }
  },
  hooks: {
    'pages:extend' (pages: NuxtPage[]) {
      appConfigurationBuilder.configureNuxtPages(pages, pagesJsonConfig.pages!, coreBuildDirectory, projectBuildDirectory)
    },
  },
  vite: {
    define: {
      'process.env.PROJECT_ID': JSON.stringify(process.env.PROJECT_ID),
      'process.env.BASE_URL': JSON.stringify(process.env.BASE_URL),
      'process.env.APP_PROJECT_META': JSON.stringify(projectMetaData),
      'process.env.NAVIGATION_TREE': JSON.stringify(pagesJsonConfig.navigationTree)
    },
    server: {
      fs: {
        allow: ['../../../']
      }
    },
    plugins: [
      pluginResolver.getViteCustomImportPathPlugin(coreBuildDirectory, projectBuildDirectory)
    ]
  },
  nitro: {
    devProxy: {
      '/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true
      }
    }
  },
  alias: {
    "@core": coreBuildDirectory
  },
  modules: [
    '@pinia/nuxt'
  ]
})
