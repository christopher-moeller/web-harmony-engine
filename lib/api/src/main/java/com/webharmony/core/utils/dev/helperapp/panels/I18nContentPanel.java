package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.i18n.I18nDataTransferService;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;
import com.webharmony.core.utils.dev.i18n.I18nClassDto;
import com.webharmony.core.utils.dev.i18n.I18nIndexBuilder;
import com.webharmony.core.utils.dev.i18n.I18nKeyDto;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nEntityAttributeTransferData;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nKeyEntryTransferData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class I18nContentPanel extends AbstractContentPanel {

    private transient List<I18nClassDto> generatedKeys = null;

    public I18nContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {

        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(createGenerateButtonPanel());

        if(generatedKeys != null) {
            verticalBox.add(createI18nResultPanel());
        }

        add(verticalBox);

    }

    private JPanel createI18nResultPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(DevHelperApp.COLOR_LIGHT);

        int countOfKeys = 0;
        final List<String[]> dataAsList = new ArrayList<>();
        for (I18nClassDto generatedKey : this.generatedKeys) {
            final String[] row = { generatedKey.getClassId(), generatedKey.getKeys().size()+"", generatedKey.getLocation().name() };
            dataAsList.add(row);
            countOfKeys += generatedKey.getKeys().size();
        }

        String[][] data = dataAsList.toArray(String[][]::new);
        JScrollPane scrollPane = createScrollPaneTable(data);

        panel.add(new Label(String.format("%s keys in %s classes found", countOfKeys, this.generatedKeys.size())), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        DevHelperButton applyButton = new DevHelperButton("Apply");
        applyButton.addActionListener(e -> {
            applyButton.setEnabled(false);
            applyKeyEntries();
        });
        panel.add(applyButton, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane createScrollPaneTable(String[][] data) {
        String[] columnNames = { "classId", "keys", "location" };

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };

        table.setFocusable(false);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {     // to detect doble click events
                    JTable target = (JTable)me.getSource();
                    int row = target.getSelectedRow(); // select a row
                    final String classId = Objects.toString(table.getValueAt(row, 0));
                    openSubPanelByClassId(classId);
                }
            }
        });

        table.setRowSelectionAllowed(true);
        return new JScrollPane(table);
    }

    protected JPanel createGenerateButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DevHelperApp.COLOR_LIGHT);
        panel.setLayout(new GridLayout(0, 3));

        DevHelperButton generateI18nButton = new DevHelperButton("Generate Key Entries");
        generateI18nButton.addActionListener(e -> generateKeyEntries());

        DevHelperButton updateTranslationsButton = new DevHelperButton("Update Translation JSONs");
        updateTranslationsButton.addActionListener(e -> updateTranslationJsons());


        panel.add(new JLabel(""));
        panel.add(generateI18nButton);
        panel.add(updateTranslationsButton);

        return panel;
    }

    private void generateKeyEntries() {
        final I18nIndexBuilder i18nBuilder = new I18nIndexBuilder();
        generatedKeys = i18nBuilder.buildStaticKeys();
        this.refresh();
    }

    private void applyKeyEntries() {
        new I18nIndexBuilder().writeJsonFiles(generatedKeys);
    }

    private void openSubPanelByClassId(String classId) {
        final I18nClassDto i18nClassDto = this.generatedKeys.stream().filter(k -> k.getClassId().equals(classId)).findAny().orElseThrow();
        getContentPanelWrapper().pushSubPanel(new I18nClassDtoSubPanel(getContentPanelWrapper(), i18nClassDto));
    }

    private static class I18nClassDtoSubPanel extends AbstractContentSubPanel {

        private final transient I18nClassDto classDto;
        protected I18nClassDtoSubPanel(ContentPanelWrapper contentPanelWrapper, I18nClassDto classDto) {
            super(contentPanelWrapper);
            this.classDto = classDto;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(classDto == null)
                return;

            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(createKeyValueLabel("Class ID", classDto.getClassId()));
            verticalBox.add(createKeyValueLabel("Keys", classDto.getKeys().size()));
            verticalBox.add(createKeyValueLabel("Location", classDto.getLocation()));
            verticalBox.add(createTableComponent());


            add(verticalBox);
        }

        private Component createTableComponent() {

            final List<String[]> dataAsList = new ArrayList<>();
            for (I18nKeyDto generatedKey : this.classDto.getKeys()) {
                final String[] row = { generatedKey.getId(), generatedKey.getPlaceholders().size()+"", generatedKey.getIsCoreEntry()+"", generatedKey.getEnglishDefaultValue(), generatedKey.getCodeLocations().size()+"" };
                dataAsList.add(row);
            }

            String[][] data = dataAsList.toArray(String[][]::new);
            String[] columnNames = { "id", "placeholders", "isCoreEntry", "englishDefaultValue", "codeLocations" };

            JTable table = createTable(data, columnNames);
            return new JScrollPane(table);
        }

        private JTable createTable(String[][] data, String[] columnNames) {
            JTable table = new JTable(data, columnNames) {
                @Override
                public boolean editCellAt(int row, int column, java.util.EventObject e) {
                    return false;
                }
            };

            table.setFocusable(false);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                    if (me.getClickCount() == 2) {
                        JTable target = (JTable)me.getSource();
                        int row = target.getSelectedRow();
                        final String keyId = Objects.toString(table.getValueAt(row, 0));
                        openSubPanelByKeyId(keyId);
                    }
                }
            });

            table.setRowSelectionAllowed(true);
            return table;
        }

        private void openSubPanelByKeyId(String keyId) {
            I18nKeyDto i18nKeyDto = this.classDto.getKeys().stream().filter(k -> k.getId().equals(keyId)).findAny().orElseThrow();
            getContentPanelWrapper().pushSubPanel(new I18nKeySubPanel(getContentPanelWrapper(), i18nKeyDto));
        }
    }

    private static class I18nKeySubPanel extends AbstractContentSubPanel {

        private final transient I18nKeyDto keyDto;
        protected I18nKeySubPanel(ContentPanelWrapper contentPanelWrapper, I18nKeyDto keyDto) {
            super(contentPanelWrapper);
            this.keyDto = keyDto;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(keyDto == null)
                return;

            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(createKeyValueTextField("ID", keyDto.getId()));
            verticalBox.add(createKeyValueTextField("IsCore Entry", keyDto.getIsCoreEntry()));
            verticalBox.add(createKeyValueTextField("English Default Value", keyDto.getEnglishDefaultValue()));
            verticalBox.add(createKeyValueTextField("Placeholders", String.join(", ", keyDto.getPlaceholders())));
            verticalBox.add(createKeyValueTextField("Code Locations", keyDto.getCodeLocations().size()));


            String[][] data = keyDto.getCodeLocations().stream().map(e -> new String[]{e}).toArray(String[][]::new);
            String[] columnNames = { "Code Location" };

            JTable table = new JTable(data, columnNames);

            verticalBox.add(table);
            add(verticalBox);
        }
    }

    private void updateTranslationJsons() {
        log.info("Start writing translation files");
        updateKeyEntriesJsons();
        updateEnumEntriesJsons();
        log.info("Writing translation files done");
    }

    private void updateKeyEntriesJsons() {

        final I18nDataTransferService dataTransferService = ContextHolder.getContext().getBean(I18nDataTransferService.class);

        final List<I18nKeyEntryTransferData> coreData = dataTransferService.createKeyEntryExportData(true, false);
        final List<I18nKeyEntryTransferData> projectData = dataTransferService.createKeyEntryExportData(false, true);

        writeDataToFile(coreData, buildEmptyFileByResource(dataTransferService.getStaticCoreTranslations(), true));
        writeDataToFile(projectData, buildEmptyFileByResource(dataTransferService.getStaticProjectTranslations(), false));

    }

    private void updateEnumEntriesJsons() {
        final I18nDataTransferService dataTransferService = ContextHolder.getContext().getBean(I18nDataTransferService.class);

        final List<I18nEntityAttributeTransferData> coreData = dataTransferService.createEntityAttributeTransferData(true, false);
        final List<I18nEntityAttributeTransferData> projectData = dataTransferService.createEntityAttributeTransferData(false, true);

        writeDataToFile(coreData, buildEmptyFileByResource(dataTransferService.getEntityAttributeCoreTranslations(), true));
        writeDataToFile(projectData, buildEmptyFileByResource(dataTransferService.getEntityAttributeProjectTranslations(), false));
    }

    @SneakyThrows
    private File buildEmptyFileByResource(Resource resource, boolean isInCoreFolder) {
        final Path rootPath = isInCoreFolder ? DevUtils.getPathToCoreRootFolder() : DevUtils.getPathToProjectRootFolder();
        final Path resourcesFolderPath = rootPath.resolve("src").resolve("main").resolve("resources");
        final Path pathToResource = resourcesFolderPath.resolve(((ClassPathResource) resource).getPath());
        final File file = pathToResource.toFile();
        if(file.exists()) {
            Files.delete(pathToResource);
        }
        File parentFolder = pathToResource.getParent().toFile();
        if(!parentFolder.exists()) {
            Files.createDirectories(parentFolder.toPath());
        }

        Files.createFile(pathToResource);
        return file;
    }

    @SneakyThrows
    private void writeDataToFile(Object data, File file) {
        JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(file, data);
    }

}
