package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.api.rest.model.DevApiLibraryBuildDto;
import com.webharmony.core.utils.dev.apilib.ApiLibraryBuilder;
import com.webharmony.core.utils.dev.apilib.api.ApiEndPointInfoBuilder;
import com.webharmony.core.utils.dev.apilib.api.ApiEndpointInfo;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ApiLibraryContentPanel extends AbstractContentPanel {

    private transient DevApiLibraryBuildDto libraryBuildDto = null;

    public ApiLibraryContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(createGenerateButtonPanel());

        if(libraryBuildDto != null) {
            verticalBox.add(createApiLibResultPanel());
        }

        add(verticalBox);
    }

    protected JPanel createGenerateButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DevHelperApp.COLOR_LIGHT);
        panel.setLayout(new GridLayout(0, 3));

        DevHelperButton generateApiLibsButton = new DevHelperButton("Generate");
        generateApiLibsButton.addActionListener(e -> generateApiSourceCode());

        panel.add(new JLabel(""));
        panel.add(generateApiLibsButton);
        return panel;
    }

    private JPanel createApiLibResultPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DevHelperApp.COLOR_LIGHT);
        panel.setLayout(new GridLayout(0, 3));

        // row 1
        panel.add(new Label("Project Lib"));
        panel.add(new Label(""));
        panel.add(new Label("Core Lib"));


        // row 2
        DevHelperButton showProjectLibButton = new DevHelperButton("Show Project Lib");
        showProjectLibButton.addActionListener(e -> getContentPanelWrapper().pushSubPanel(new ApiSourceCodePreview(getContentPanelWrapper(), "Project Source", libraryBuildDto.getProjectSourceCode())));
        panel.add(showProjectLibButton);

        panel.add(new Label(""));

        DevHelperButton showCoreLibButton = new DevHelperButton("Show Core Lib");
        showCoreLibButton.addActionListener(e -> getContentPanelWrapper().pushSubPanel(new ApiSourceCodePreview(getContentPanelWrapper(), "Core Source", libraryBuildDto.getCoreSourceCode())));
        panel.add(showCoreLibButton);

        // row 3
        DevHelperButton applyProjectLibButton = new DevHelperButton("Apply Project Lib");
        applyProjectLibButton.addActionListener(e -> {
            applyProjectLibButton.setEnabled(false);
            new ApiLibraryBuilder().saveProjectLibFile(this.libraryBuildDto.getProjectSourceCode());
        });
        panel.add(applyProjectLibButton);

        panel.add(new Label(""));

        DevHelperButton applyCoreLibButton = new DevHelperButton("Apply Core Lib");
        applyCoreLibButton.addActionListener(e -> {
            applyCoreLibButton.setEnabled(false);
            new ApiLibraryBuilder().saveCoreLibFile(this.libraryBuildDto.getCoreSourceCode());
        });
        panel.add(applyCoreLibButton);

        panel.add(new Label(""));
        panel.add(new Label(""));
        panel.add(new Label(""));

        // Router pages
        panel.add(new Label("Router Pages JSON"));
        panel.add(new Label(""));
        panel.add(new Label(""));
        DevHelperButton showRouterPageJson = new DevHelperButton("Show Project-Configuration JSON");
        showRouterPageJson.addActionListener(e -> getContentPanelWrapper().pushSubPanel(new ApiSourceCodePreview(getContentPanelWrapper(), "Project-Configuration JSON", libraryBuildDto.getProjectConfigurationJson())));
        panel.add(showRouterPageJson);
        panel.add(new Label(""));
        panel.add(new Label(""));
        DevHelperButton applyProjectConfigurationButton = new DevHelperButton("Apply Project-Configuration JSON");
        applyProjectConfigurationButton.addActionListener(e -> {
            applyProjectConfigurationButton.setEnabled(false);
            new ApiLibraryBuilder().saveProjectConfigurationJSONFile(this.libraryBuildDto.getProjectConfigurationJson());
        });
        panel.add(applyProjectConfigurationButton);
        panel.add(new Label(""));

        return panel;
    }

    private void generateApiSourceCode() {
        this.libraryBuildDto = buildApiLibSrc();
        this.refresh();
    }

    private DevApiLibraryBuildDto buildApiLibSrc() {
        final ApiEndPointInfoBuilder builder = new ApiEndPointInfoBuilder();
        final List<ApiEndpointInfo> allApiEndpoints = builder.getAllApiEndpoints();
        return new ApiLibraryBuilder(allApiEndpoints).createLib();
    }

    private static class ApiSourceCodePreview extends AbstractContentSubPanel {

        private final String sourceCode;
        private final String title;
        protected ApiSourceCodePreview(ContentPanelWrapper contentPanelWrapper, String title, String sourceCode) {
            super(contentPanelWrapper);
            this.sourceCode = sourceCode;
            this.title = title;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(sourceCode != null) {
                add(createRichReadOnlyTextAreaPanel(title, sourceCode));
            }
        }
    }
}
