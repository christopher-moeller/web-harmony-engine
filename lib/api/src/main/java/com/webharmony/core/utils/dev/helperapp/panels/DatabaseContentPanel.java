package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.configuration.flyway.FlywayManager;
import com.webharmony.core.context.AppInitializationRunner;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.demo.DemoDataService;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;

import javax.swing.*;
import java.awt.*;

public class DatabaseContentPanel extends AbstractContentPanel {

    public DatabaseContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(createActionButtonPanel());

        add(verticalBox);
    }

    protected JPanel createActionButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DevHelperApp.COLOR_LIGHT);
        panel.setLayout(new GridLayout(0, 3));

        DevHelperButton clearButton = new DevHelperButton("Clear Database");
        clearButton.addActionListener(e -> clearDatabase());

        DevHelperButton resetButton = new DevHelperButton("Reset initial state");
        resetButton.addActionListener(e -> resetData());

        DevHelperButton resetWithDemoButton = new DevHelperButton("Reset initial state with demo data");
        resetWithDemoButton.addActionListener(e -> resetInitialStateWithDemoData());

        panel.add(clearButton);
        panel.add(resetButton);
        panel.add(resetWithDemoButton);
        return panel;
    }

    private void clearDatabase() {
        final FlywayManager flywayManager = ContextHolder.getContext().getBean(FlywayManager.class);
        flywayManager.clean();
    }
    private void resetData() {
        final FlywayManager flywayManager = ContextHolder.getContext().getBean(FlywayManager.class);
        flywayManager.clean();
        flywayManager.migrate();
        ContextHolder.getContext().getBean(AppInitializationRunner.class).initialize(ContextHolder.getContext());
    }

    private void resetInitialStateWithDemoData() {
        resetData();
        ContextHolder.getContext().getBean(DemoDataService.class).initializeDemoData();
    }


}
