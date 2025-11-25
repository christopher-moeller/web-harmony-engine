package com.webharmony.core.service.email;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ThymeleafEmail {

    private String toEmail;
    private String subject;
    private EThymeleafTemplate template;
    private Map<String, Object> variables;


}
