package com.webharmony.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.error.ApiError;
import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.api.rest.model.LoginResult;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.configuration.EApplicationStatus;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.testutils.ETestUser;
import com.webharmony.core.testutils.TestUserAuthentication;
import com.webharmony.core.testutils.annotations.WithTestUser;
import com.webharmony.core.testutils.annotations.WithoutAuthentication;
import com.webharmony.core.utils.JacksonUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TestAppInitializer.class, AbstractAppMain.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractBaseTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ActorService actorService;

    @PersistenceContext
    protected EntityManager em;


    static final PostgreSQLContainer<?> postgreSQLContainer = initSQLContainer();

    private String currentUserToken = null;

    @SuppressWarnings("all")
    private static PostgreSQLContainer<?> initSQLContainer() {
        PostgreSQLContainer<?> instance = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
                .withDatabaseName("test")
                .withUsername("duke")
                .withPassword("s3cret")
                .withReuse(true);

        instance.start();
        return instance;
    }

    @DynamicPropertySource
    static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    protected void beforeEach(TestInfo testInfo) {
        actorService.clearCache();
        this.clearAuthenticatedUser();

        final Method testMethod = testInfo.getTestMethod().orElse(null);
        if(testMethod == null || testMethod.getAnnotation(WithoutAuthentication.class) != null) {
            return;
        }

        Optional.ofNullable(testMethod.getAnnotation(WithTestUser.class)).ifPresentOrElse(annotation -> {
            ETestUser eTestUser = annotation.value();
            setAuthenticatedUser(eTestUser);
        }, () -> setAuthenticatedUser(ETestUser.ADMIN_USER));

        initAuthenticationToken();
        AppStatusHolder.getInstance().setNewStatus(EApplicationStatus.OK, "");
    }

    @AfterEach
    @SuppressWarnings("all")
    protected void afterEach(TestInfo testInfo) {
        clearAuthenticatedUser();
    }

    protected void setAuthenticatedUser(ETestUser eTestUser) {
        AppUser appUser = eTestUser.loadUser();
        SecurityContextHolder.getContext().setAuthentication(new TestUserAuthentication(appUser));
    }

    protected void initAuthenticationToken() {
        AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
        if(currentActor instanceof AppUserActor userActor) {

            final AuthenticationService authService = ContextHolder.getContext().getBean(AuthenticationService.class);
            UserLoginVM vm = new UserLoginVM();
            vm.setUsername(userActor.getUser().getEmail());
            vm.setPassword("*");

            final LoginResult loginResult = authService.loginUser(vm);
            this.currentUserToken = loginResult.getToken();
        }

    }

    protected void clearAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
        this.currentUserToken = null;
    }

    @SneakyThrows
    @SuppressWarnings("unused")
    protected <T> T getRequest(String link, Class<T> responseType) {
        return executeHttpRequest(HttpMethod.GET, link, null, responseType, null);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    protected <T> T getRequest(String link, Class<T> responseType, HttpStatus exceptStatus) {
        return executeHttpRequest(HttpMethod.GET, link, null, responseType, exceptStatus);
    }

    @SuppressWarnings("all")
    protected <T> T postRequest(String link, Object body, Class<T> responseType) {
        return postRequest(link, body, responseType, HttpStatus.OK);
    }

    @SneakyThrows
    protected <T> T postRequest(String link, Object body, Class<T> responseType, HttpStatus exceptStatus) {
        return executeHttpRequest(HttpMethod.POST, link, body, responseType, exceptStatus);
    }

    @SneakyThrows
    protected <T> T executeHttpRequest(HttpMethod httpMethod, String link, Object body, Class<T> responseType, HttpStatus exceptStatus) {
        final MockHttpServletRequestBuilder requestBuilder;
        if(httpMethod.equals(HttpMethod.GET)) {
            requestBuilder = get(link);
        } else if(httpMethod.equals(HttpMethod.POST)) {
            requestBuilder = post(link);
        } else {
            throw new RuntimeException(String.format("Http method '%s 'not supported", httpMethod.name()));
        }

        final ObjectMapper jacksonMapper = JacksonUtils.createDefaultJsonMapper();

        requestBuilder.contentType("application/json");

        if(body != null) {
            requestBuilder.content(jacksonMapper.writeValueAsString(body));
        }

        if(currentUserToken != null)
            requestBuilder.header("Authentication", "Bearer "+currentUserToken);

        final ResultMatcher statusMatcher;
        if(exceptStatus == null || exceptStatus.equals(HttpStatus.OK)) {
            statusMatcher = status().isOk();
        } else if(exceptStatus.equals(HttpStatus.BAD_REQUEST)) {
            statusMatcher = status().isBadRequest();
        } else if(exceptStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            statusMatcher = status().isInternalServerError();
        } else if(exceptStatus.equals(HttpStatus.FORBIDDEN)) {
            statusMatcher = status().isForbidden();
        }else {
            throw new RuntimeException(String.format("Http status '%s' not available", exceptStatus.name()));
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(statusMatcher);

        if(!responseType.equals(Void.class)) {
            resultActions = resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        final MvcResult mvcResult = resultActions.andReturn();

        if(responseType.equals(Void.class)) {
            return null;
        } else {
            return jacksonMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), responseType);
        }
    }

    protected ValidationResultDto getValidationResultByApiError(ApiError apiError) {
        return JacksonUtils.createDefaultJsonMapper().convertValue(apiError.getData(), ValidationResultDto.class);
    }

    protected Long getCountOfEntities(EntityPathBase<?> entityPathBase) {
        return new JPAQuery<>(em)
                .select(entityPathBase.count())
                .from(entityPathBase)
                .fetchOne();
    }

}
