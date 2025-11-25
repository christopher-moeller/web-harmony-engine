package com.webharmony.core.service.email;


public enum EThymeleafTemplate {

    SIMPLE_ONE_LINK_MAIL_TEMPLATE("simple_one_link_template.html");

    private final String subPath;

    EThymeleafTemplate(String subPath) {
        this.subPath = subPath;
    }

    public String getPath() {
        return String.format("email/templates/%s", this.subPath);
    }
}
