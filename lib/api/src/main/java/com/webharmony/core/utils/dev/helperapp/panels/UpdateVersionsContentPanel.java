package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.helperapp.builder.BuildSettings;
import com.webharmony.core.utils.dev.helperapp.builder.VersionDetail;
import com.webharmony.core.utils.dev.helperapp.builder.VersionSummary;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;
import com.webharmony.core.utils.dev.helperapp.builder.VersionFileUpdater;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.Path;

public class UpdateVersionsContentPanel extends AbstractContentPanel {

    @Setter
    private static boolean isBuilding = false;

    public UpdateVersionsContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {
        final BuildSettings buildSettings = buildSettings();

        final Box box = Box.createVerticalBox();

        box.add(createKeyValueTextField("apiCoreFolder", buildSettings.getApiCoreFolder().toAbsolutePath()));
        box.add(createKeyValueTextField("apiProjectFolder", buildSettings.getApiProjectFolder().toAbsolutePath()));
        box.add(createKeyValueTextField("uiCoreFolder", buildSettings.getUiCoreFolder().toAbsolutePath()));
        box.add(createKeyValueTextField("uiProjectFolder", buildSettings.getUiProjectFolder().toAbsolutePath()));

        box.add(new Label("New Versions and builds"));

        VersionFileUpdater builder = new VersionFileUpdater(buildSettings);
        final VersionSummary versionSummary = builder.getCurrentVersionSummary();
        updateBuildNumbers(versionSummary);

        box.add(buildVersionEditComponent("API Core", versionSummary.getApiCoreVersion()));
        box.add(buildVersionEditComponent("API Project", versionSummary.getApiProjectVersion()));
        box.add(buildVersionEditComponent("UI Core", versionSummary.getUiCoreVersion()));
        box.add(buildVersionEditComponent("UI Project", versionSummary.getUiProjectVersion()));


        final DevHelperButton applyButton = new DevHelperButton("Apply");
        applyButton.setEnabled(!isBuilding);
        applyButton.addActionListener(a -> {
            setIsBuilding(true);
            refresh();
            new Thread(() -> applyNewVersionsAndBuilds(buildSettings, versionSummary)).start();
        });
        box.add(applyButton);

        if(isBuilding) {
            box.add(new Label("Currently applying..."));
        }

        add(box);
    }

    private void updateBuildNumbers(VersionSummary versionSummary) {
        updateBuildNumber(versionSummary.getApiCoreVersion());
        updateBuildNumber(versionSummary.getApiProjectVersion());
        updateBuildNumber(versionSummary.getUiCoreVersion());
        updateBuildNumber(versionSummary.getUiProjectVersion());
    }

    private void updateBuildNumber(VersionDetail versionDetail) {
        int build = versionDetail.getBuild();
        versionDetail.setBuild(build + 1);
    }

    private Component buildVersionEditComponent(String caption, VersionDetail versionDetail) {
        Box box = Box.createHorizontalBox();
        box.add(new Label(caption));
        box.add(new Label(String.format("Build: %s", versionDetail.getBuild())));

        final JTextField textField = new JTextField();
        textField.setText(versionDetail.getVersion());
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                versionDetail.setVersion(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                versionDetail.setVersion(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                versionDetail.setVersion(textField.getText());
            }
        });

        box.add(textField);
        return box;
    }

    @SneakyThrows
    private void applyNewVersionsAndBuilds(BuildSettings buildSettings, VersionSummary versionSummary) {

        VersionFileUpdater builder = new VersionFileUpdater(buildSettings);
        builder.writeVersionFiles(versionSummary);

        setIsBuilding(false);
        refresh();
    }

    private BuildSettings buildSettings() {
        final BuildSettings buildSettings = new BuildSettings();
        buildSettings.setApiCoreFolder(DevUtils.getPathToCoreRootFolder());
        buildSettings.setApiProjectFolder(DevUtils.getPathToProjectRootFolder());

        final Path webharmonyFolder = buildSettings.getApiCoreFolder().getParent().getParent();
        buildSettings.setWebharmonyRootFolder(webharmonyFolder);
        buildSettings.setUiCoreFolder(webharmonyFolder.resolve("ui").resolve("webharmony-nuxt-core"));

        final String projectFolderName = buildSettings.getApiProjectFolder().toFile().getName();
        buildSettings.setUiProjectFolder(webharmonyFolder.resolve("ui").resolve("projects").resolve(projectFolderName));

        return buildSettings;
    }


}
