package com.webharmony.core.configuration.security;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Optional;

@Configuration
public class WebSecurityConfiguration {


    @Bean
    @SuppressWarnings("all")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().disable()
                .csrf().disable().authorizeHttpRequests()
                .requestMatchers("/**")
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw  new InternalServerException("This method is not supported");
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("coreHandlerMethodAuthorizationResolver")
    public HandlerMethodAuthorizationResolver coreHandlerMethodAuthorizationResolver() {
        return handlerMethod -> Optional.ofNullable(handlerMethod.getMethodAnnotation(CoreApiAuthorization.class))
                .map(a -> ApplicationAccessRule.of(a.value(), a.isOrConnected()))
                .orElse(null);
    }

    public void validateRestApis(ApplicationContext applicationContext) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        final ActorRightService actorRightService = applicationContext.getBean(ActorRightService.class);
        for (HandlerMethod handlerMethod : requestMappingHandlerMapping.getHandlerMethods().values()) {
            if(AbstractBaseController.class.isAssignableFrom(handlerMethod.getBeanType())) {
                boolean isSecuredApiMethod = ((AbstractBaseController) applicationContext.getBean(handlerMethod.getBeanType()))
                        .isSecuredApiMethod(handlerMethod.getMethod());

                if(isSecuredApiMethod)
                    continue;
            }

            Assert.isNotNull(actorRightService.getAccessRuleForHandlerMethod(handlerMethod))
                    .withException(() -> new InternalServerException(String.format("Handler method '%s is not secured", handlerMethod)))
                    .verify();
        }
    }
}
