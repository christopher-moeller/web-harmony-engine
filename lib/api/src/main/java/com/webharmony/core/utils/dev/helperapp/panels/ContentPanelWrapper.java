package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContentPanelWrapper extends JPanel {

    private AbstractContentPanel currentBasePanel = null;

    private final List<AbstractContentSubPanel> stackedSubPanels = new ArrayList<>();

    public ContentPanelWrapper() {
        setLayout(new BorderLayout());
    }

    public void setContentPanel(AbstractContentPanel contentPanel) {

        if(currentBasePanel != null && currentBasePanel.getClass() == contentPanel.getClass()) {
            return;
        } else {
            this.stackedSubPanels.clear();
            this.currentBasePanel = contentPanel;
        }

        initPanel(this.currentBasePanel);
    }

    private void initPanel(AbstractContentPanel contentPanel) {
        removeAll();
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(DevHelperApp.COLOR_GREY);
        header.setLayout(new GridLayout());

        Component leftComponent = this.stackedSubPanels.isEmpty() ? new Label("") : createPopSubPanelButton();
        Component rightComponent = createRefreshButton();

        header.add(leftComponent, BorderLayout.WEST);
        header.add(new Label(""), BorderLayout.CENTER);
        header.add(rightComponent, BorderLayout.EAST);

        return header;
    }

    private DevHelperButton createRefreshButton() {
        DevHelperButton button = new DevHelperButton("Refresh");
        button.addActionListener(e -> getCurrentlyVisiblePanel().refresh());
        return button;
    }

    private AbstractContentPanel getCurrentlyVisiblePanel() {
        if(this.stackedSubPanels.isEmpty()) {
            return this.currentBasePanel;
        } else {
            return this.stackedSubPanels.get(this.stackedSubPanels.size() - 1);
        }
    }

    private JButton createPopSubPanelButton() {
        JButton button = new JButton("Back");
        button.addActionListener(e -> popSubPanel());
        return button;
    }

    public void pushSubPanel(AbstractContentSubPanel subPanel) {
        this.stackedSubPanels.add(subPanel);
        initPanel(subPanel);
    }

    public void popSubPanel() {
        if(!this.stackedSubPanels.isEmpty()) {
            this.stackedSubPanels.remove(this.stackedSubPanels.size() - 1);
            if(this.stackedSubPanels.isEmpty()) {
                initPanel(this.currentBasePanel);
            } else {
                initPanel(this.stackedSubPanels.get(this.stackedSubPanels.size() - 1));
            }
        }
    }

}
