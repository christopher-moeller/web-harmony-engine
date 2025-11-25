package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.api.rest.model.view.ApplicationStatusVM;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.context.FrontendInfo;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;

import javax.swing.*;
import java.awt.*;

public class StatusContentPanel extends AbstractContentPanel {

    public StatusContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {
        setLayout(new GridLayout(10,1 ));
        add(createStatusPanel());
        add(createActionPanel());
    }

    private JPanel createStatusPanel() {
        ApplicationStatusVM currentStatus = AppStatusHolder.getInstance().getCurrentStatus();

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(DevHelperApp.COLOR_LIGHT);
        statusPanel.setLayout(new GridLayout(0, 4));
        statusPanel.add(createKeyValueLabel("Status", currentStatus.getStatus()));
        statusPanel.add(createKeyValueLabel("User Message", currentStatus.getUserMessage()));
        statusPanel.add(createKeyValueLabel("Technical Message", currentStatus.getTechnicalMessage()));
        return statusPanel;
    }

    private JPanel createActionPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(DevHelperApp.COLOR_LIGHT);
        statusPanel.setLayout(new GridLayout(0, 4));
        statusPanel.add(createOpenBrowserButton("Local Frontend", FrontendInfo::getFrontendUrl));
        statusPanel.add(createOpenBrowserButton("Open Swagger UI", fi -> fi.getServerInfo().getSwaggerUrl()));
        return statusPanel;
    }

}
