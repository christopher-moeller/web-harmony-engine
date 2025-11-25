package com.webharmony.core.service.email;

import com.webharmony.core.context.ArtifactInfo;
import com.webharmony.core.context.FrontendInfo;
import com.webharmony.core.service.email.data.SimpleOneLinkMailData;
import com.webharmony.core.utils.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateService {

    private final FrontendInfo frontendInfo;

    private final EmailService emailService;

    private final ArtifactInfo artifactInfo;

    public EmailTemplateService(FrontendInfo frontendInfo, EmailService emailService, ArtifactInfo artifactInfo) {
        this.frontendInfo = frontendInfo;
        this.emailService = emailService;
        this.artifactInfo = artifactInfo;
    }

    public void sendBaseOneLinkMail(SimpleOneLinkMailData config) {
        Map<String, Object> data = new HashMap<>();
        data.put("mainLogoSvg", getSvgLogoSrc());
        data.put("projectName", artifactInfo.getLongName());
        data.put("caption", config.getCaption());
        data.put("message", config.getMessage());
        data.put("btnText", config.getBtnText());
        data.put("btnLink", config.getBtnLink());

        ThymeleafEmail thymeleafEmail = new ThymeleafEmail();
        thymeleafEmail.setToEmail(config.getEmailToAddress());
        thymeleafEmail.setSubject(config.getEmailSubject());
        thymeleafEmail.setTemplate(EThymeleafTemplate.SIMPLE_ONE_LINK_MAIL_TEMPLATE);
        thymeleafEmail.setVariables(data);

        emailService.sendEmail(thymeleafEmail);
    }

    private String getSvgLogoSrc() {
        final Resource logoImageFile = frontendInfo.getServerInfo().getLogoImageFile();
        return FileUtils.readResourceAsString(logoImageFile);
    }




}
