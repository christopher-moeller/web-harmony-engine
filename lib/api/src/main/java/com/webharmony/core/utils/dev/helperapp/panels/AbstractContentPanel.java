package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.context.FrontendInfo;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public abstract class AbstractContentPanel extends JPanel {

    private final ContentPanelWrapper contentPanelWrapper;

    protected AbstractContentPanel(ContentPanelWrapper contentPanelWrapper) {
        this.contentPanelWrapper = contentPanelWrapper;
        init();
    }

    private void init() {
        setBackground(DevHelperApp.COLOR_LIGHT);
        removeAll();
        initContent();
        revalidate();
        repaint();
    }

    public void refresh() {
        init();
    }

    protected void initContent() {
        add(new JLabel(this.getClass().getName()));
    }

    protected JLabel createKeyValueLabel(String key, Object value) {
        final String stringValue = Objects.toString(value);
        return new JLabel(String.format("<html><b>%s</b>: %s</html>", key, stringValue));
    }

    protected Component createKeyValueTextField(String key, Object value) {
        final String stringValue = Objects.toString(value);
        Box box = Box.createHorizontalBox();
        box.add(new JLabel(String.format("<html><b>%s</b></html>", key)));

        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(300, 20));
        textField.setMaximumSize(new Dimension(300, 20));
        textField.setText(stringValue);

        box.add(textField);
        return box;
    }

    protected DevHelperButton createOpenBrowserButton(String caption, Function<FrontendInfo, String> urlResolver) {
        DevHelperButton button = new DevHelperButton(caption);
        button.addActionListener(e -> {
            FrontendInfo frontendInfo = ContextHolder.getContext().getBean(FrontendInfo.class);
            openBrowserWithUrl(urlResolver.apply(frontendInfo));
        });
        return button;
    }

    @SneakyThrows
    private void openBrowserWithUrl(String url) {
        Desktop.getDesktop().browse(new URL(url).toURI());
    }

    protected JScrollPane createScrollableTable(String[] columnNames, String[][] data, Consumer<String> onRowClick) {
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
                    final String keyId = Objects.toString(table.getValueAt(row, 0));
                    onRowClick.accept(keyId);
                }
            }
        });

        table.setRowSelectionAllowed(true);
        return new JScrollPane(table);
    }

    protected JPanel createRichReadOnlyTextAreaPanel(String title, String content) {

        JTextArea jTextArea = new JTextArea();
        jTextArea.setText(content);
        jTextArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(0, 2));
        headerPanel.add(new Label(title));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(wrapInScrollPane(jTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane wrapInScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700,600));
        return scrollPane;
    }
}
