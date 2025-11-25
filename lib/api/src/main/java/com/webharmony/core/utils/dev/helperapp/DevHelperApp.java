package com.webharmony.core.utils.dev.helperapp;


import java.awt.*;

public class DevHelperApp {

    public static final Color COLOR_PRIMARY = Color.decode("#424242");
    public static final Color COLOR_LIGHT = Color.decode("#f6f6f9");

    public static final Color COLOR_GREY = Color.decode("#eeeeee");

    private static DevHelperApp instance = null;

    private final DevHelperFrame frame;

    private DevHelperApp() {
        frame = new DevHelperFrame();
    }

    public static DevHelperApp getInstance() {
        if(instance == null) {
            instance = new DevHelperApp();
        }
        return instance;
    }

    public void show() {
        frame.setVisible(true);
    }

}
