package com.webharmony.core.service.email;

import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.enums.ECoreSecureKey;
import com.webharmony.core.data.jpa.model.email.AppEmail;
import com.webharmony.core.data.jpa.model.email.EMailState;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.service.data.EmailCrudService;
import com.webharmony.core.service.data.FileService;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class EmailService {

    private final EmailCrudService emailCrudService;

    private final SpringTemplateEngine emailTemplateEngine;

    private final FileService fileService;

    @Value( "${email.sendEmails}" )
    private boolean sendEmails;


    public EmailService(EmailCrudService emailCrudService, SpringTemplateEngine emailTemplateEngine, FileService fileService) {
        this.emailCrudService = emailCrudService;
        this.emailTemplateEngine = emailTemplateEngine;
        this.fileService = fileService;
    }

    @Transactional
    public void sendEmail(UUID emailUUID) {
        sendEmail(emailCrudService.getEntityById(emailUUID));
    }

    public void sendEmail(ThymeleafEmail thymeleafEmail) {

        AppEmail appEmail = new AppEmail();
        appEmail.setState(EMailState.CREATED);
        appEmail.setFromEmail(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.getNotEmptyKey());
        appEmail.setToEmail(Objects.requireNonNull(thymeleafEmail.getToEmail()));
        appEmail.setSubject(Objects.requireNonNull(thymeleafEmail.getSubject()));

        EThymeleafTemplate template = Objects.requireNonNull(thymeleafEmail.getTemplate());
        Map<String, Object> variables = Optional.ofNullable(thymeleafEmail.getVariables()).orElseGet(HashMap::new);
        appEmail.setHtmlMessage(generateHtmlContent(template, variables));

        sendEmail(emailCrudService.saveEntity(appEmail));
    }

    @SneakyThrows
    public void sendEmail(AppEmail appEmail) {

        if(!ECoreRegistry.EMAIL_SEND_MAILS.getBooleanValue()) {
            log.info("Sending of emails is currently deactivated");
            return;
        }

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom(new InternetAddress(appEmail.getFromEmail()));
        mimeMessageHelper.setTo(InternetAddress.parse(appEmail.getToEmail()));
        mimeMessageHelper.setSubject(appEmail.getSubject());
        mimeMessageHelper.setText(appEmail.getHtmlMessage(), true);

        for (AppFile attachment : appEmail.getAttachments()) {
            mimeMessageHelper.addAttachment(attachment.getFileName(), fileService.getResourceByFile(attachment));
        }

        sendMessage(message);

        appEmail.setLastSending(LocalDateTime.now());
        emailCrudService.saveEntity(appEmail);
    }

    @SneakyThrows
    private void sendMessage(MimeMessage message) {
        if(sendEmails) {
            Transport.send(message);
        } else {
            log.info("E-Mail sending is deactivated");
        }
    }

    private Session createSession() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", ECoreSecureKey.E_MAIL_SERVER_HOST.getNotEmptyKey());
        props.put("mail.smtp.port", "587");

        final String username = ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.getNotEmptyKey();
        final String password = ECoreSecureKey.E_MAIL_SERVER_PASSWORD.getNotEmptyKey();
        return Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    private String generateHtmlContent(EThymeleafTemplate template, Map<String, Object> variables){
        Context ctx = prepareContextByEmail(variables);
        return this.emailTemplateEngine.process(template.getPath(), ctx);
    }


    private Context prepareContextByEmail(Map<String, Object> variables){
        Context ctx = new Context();
        ctx.setVariables(variables);
        return ctx;
    }

}
