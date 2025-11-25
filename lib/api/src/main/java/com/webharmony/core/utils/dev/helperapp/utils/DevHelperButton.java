package com.webharmony.core.utils.dev.helperapp.utils;

import javax.swing.*;
import java.awt.*;

public class DevHelperButton extends JButton {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;

    public DevHelperButton(String caption) {
        super(caption);
        setButtonSize();
    }

    private void setButtonSize() {
        Dimension dimension = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
    }
}
