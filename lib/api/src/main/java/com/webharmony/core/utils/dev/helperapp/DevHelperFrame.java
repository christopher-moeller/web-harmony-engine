package com.webharmony.core.utils.dev.helperapp;

import com.webharmony.core.utils.dev.helperapp.panels.*;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DevHelperFrame extends JFrame {

    private static final ENavigationSteps INITIAL_STEP = ENavigationSteps.STATUS;

    public DevHelperFrame() {
        setTitle("Harmony Development Helper");
        setSize(1000, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        Header header = new Header();
        header.setHeaderTitle(INITIAL_STEP.getLabel());
        add(header, BorderLayout.NORTH);

        ContentPanelWrapper contentPanelWrapper = new ContentPanelWrapper();
        contentPanelWrapper.setContentPanel(INITIAL_STEP.createNewContentPanelInstance(contentPanelWrapper));
        add(contentPanelWrapper, BorderLayout.CENTER);

        NavigationSidebar navigationSidebar = new NavigationSidebar();
        navigationSidebar.setOnNavigate(step -> {
            header.setHeaderTitle(step.getLabel());
            contentPanelWrapper.setContentPanel(step.createNewContentPanelInstance(contentPanelWrapper));
        });
        add(navigationSidebar, BorderLayout.WEST);


        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DevHelperFrame::new);
    }


    @AllArgsConstructor
    @Getter
    enum ENavigationSteps {

        STATUS("Status", StatusContentPanel.class),
        DB("Database", DatabaseContentPanel.class),
        API_LIBRARY("Api Library", ApiLibraryContentPanel.class),
        I18N("I18N", I18nContentPanel.class),
        RESOURCES_VIEW_MODELS("Resources & View Models", ResourcesAndViewModelsContentPanel.class),
        BUILD_ARTIFACT("Update Versions", UpdateVersionsContentPanel.class);

        private final String label;
        private final Class<? extends AbstractContentPanel> contentPanelClass;

        @SneakyThrows
        public AbstractContentPanel createNewContentPanelInstance(ContentPanelWrapper wrapper) {
            return this.contentPanelClass.getDeclaredConstructor(ContentPanelWrapper.class).newInstance(wrapper);
        }

    }

    static class Header extends JPanel {

        public Header() {
            JLabel headerLabel = new JLabel("Header");
            add(headerLabel);
            setBackground(DevHelperApp.COLOR_PRIMARY);
        }

        public void setHeaderTitle(String headerTitle) {
            removeAll();
            JLabel headerLabel = new JLabel(headerTitle);
            headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            headerLabel.setForeground(DevHelperApp.COLOR_LIGHT);
            add(headerLabel);
            revalidate();
            repaint();
        }

    }

    @Setter
    static class NavigationSidebar extends JPanel {

        private transient Consumer<ENavigationSteps> onNavigate;

        public NavigationSidebar() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(DevHelperApp.COLOR_PRIMARY);

            for (ENavigationSteps navigationStep : ENavigationSteps.values()) {
                DevHelperButton button = new DevHelperButton(navigationStep.getLabel());
                button.addActionListener(e -> onStepClick(navigationStep));
                this.add(button);
            }

        }

        private void onStepClick(ENavigationSteps step) {
            if(onNavigate != null) {
                onNavigate.accept(step);
            }
        }

    }

}
