package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIPagesIndexFile extends VirtualFile {

    private static final String TEMPLATE = """
            <template>
              <HarmonyWebViewPageHeader />
              <HarmonyBaseWebLayout>
                <div id="harmony-slogan-area">
                  <div>
                    <p class="harmony-slogan"><span>Demo.</span> The place for your</p>
                    <p class="harmony-slogan">projects</p>
                  </div>
                </div>
              </HarmonyBaseWebLayout>
            </template>
                        
            <script setup lang="ts">
                        
                        
            import HarmonyWebViewPageHeader from "@core/components/view/web/HarmonyWebViewPageHeader.vue";
            import HarmonyBaseWebLayout from "@core/components/view/web/HarmonyBaseWebLayout.vue";
            </script>
                        
            <style scoped>
                        
            #harmony-slogan-area {
              display: flex;
              justify-content: center;
              margin-top: 5em;
            }
                        
            .harmony-slogan {
              font-size: 30px;
              color: grey;
            }
                        
            .harmony-slogan span{
              color: black;
            }
                        
            </style>
            """;

    public UIPagesIndexFile() {
        super("index.vue");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}
