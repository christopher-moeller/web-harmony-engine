package com.webharmony.core.api.rest;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.AuthenticationController;
import com.webharmony.core.api.rest.error.ApiError;
import com.webharmony.core.api.rest.error.ApiErrorWithJavaExceptionInstance;
import com.webharmony.core.api.rest.error.validation.ValidationFieldErrorDto;
import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.api.rest.model.AuthenticatedUserDto;
import com.webharmony.core.api.rest.model.LoginResult;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.api.rest.model.view.user.UserLogoutRequest;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.data.jpa.model.actor.QAppUserActor;
import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.QAppToken;
import com.webharmony.core.data.jpa.model.token.TokenEntityReferencePayload;
import com.webharmony.core.data.jpa.model.user.QAppActorRight;
import com.webharmony.core.service.authentication.UserLoginBlockService;
import com.webharmony.core.service.authentication.types.UserBlockedEntry;
import com.webharmony.core.testutils.ETestUser;
import com.webharmony.core.testutils.annotations.WithTestUser;
import com.webharmony.core.testutils.annotations.WithoutAuthentication;
import com.webharmony.core.utils.reflection.ApiLink;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class AuthenticationControllerTest extends AbstractApiTest<AuthenticationController> {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserLoginBlockService userLoginBlockService;

    @Override
    @SneakyThrows
    @AfterEach
    @SuppressWarnings("unchecked")
    protected void afterEach(TestInfo testInfo) {
        super.afterEach(testInfo);

        final Field usernameBlockedMapField = UserLoginBlockService.class.getDeclaredField("usernameBlockedMap");
        usernameBlockedMapField.setAccessible(true);
        final Map<String, UserBlockedEntry> userBlockmap = (Map<String, UserBlockedEntry>) usernameBlockedMapField.get(userLoginBlockService);
        userBlockmap.clear();
    }

    @Test
    void shouldBeAbleToLoginForSimpleUser() {

        final LoginResult loginResult = loginUser(ETestUser.BASE_USER_1.getEmail(), "*");
        assertThat(loginResult.getType()).isEqualTo("Bearer");
        assertThat(loginResult.getToken()).isNotEmpty();
        assertThat(loginResult.getTokenHeaderField()).isEqualTo("Authentication");

        final AppToken appToken = findAppTokenOrGetNull(loginResult.getToken());

        assertThat(appToken).isNotNull();

        final UUID userUUID = appToken.getTypedPayload(TokenEntityReferencePayload.class).getEntityUUID();

        final AppUserActor userActor = new JPAQuery<>(em).select(QAppUserActor.appUserActor)
                .from(QAppUserActor.appUserActor)
                .join(QAppUserActor.appUserActor.user).fetchJoin()
                .where(QAppUserActor.appUserActor.uuid.eq(userUUID))
                .fetchOne();

        assertThat(userActor).isNotNull();
        assertThat(userActor.getUser()).isNotNull();
        assertThat(userActor.getUser().getEmail() ).isEqualTo(ETestUser.BASE_USER_1.getEmail());

    }

    @Test
    void shouldNotBeAbleToLoginForSimpleUserBecauseOfWrongEmail() {

        final String userName = "wrong_user@webharmony.de";

        final ApiError apiError = loginUserAndExceptError(userName, "*");
        assertDefaultLoginValidationErrors(apiError);
    }

    @Test
    void shouldNotBeAbleToLoginForSimpleUserBecauseOfWrongPassword() {

        final ApiError apiError = loginUserAndExceptError(ETestUser.BASE_USER_1.getEmail(), "wrongPW");
        assertDefaultLoginValidationErrors(apiError);
    }

    @Test
    @WithTestUser(ETestUser.BASE_USER_1)
    void shouldGetAuthenticatedBaseUser1ByMeCall() {
        AuthenticatedUserDto baseUser1 = assertOkResponse(AuthenticationController::getOwnUser);
        assertThat(baseUser1.getUsername()).isEqualTo(ETestUser.BASE_USER_1.getEmail());
        assertThat(baseUser1.getEffectiveRights()).hasSameSizeAs(ETestUser.BASE_USER_1.getActorRights());
    }

    @Test
    @WithTestUser(ETestUser.ADMIN_USER)
    void shouldGetAuthenticatedAdminUserByMeCall() {
        AuthenticatedUserDto adminUser = assertOkResponse(AuthenticationController::getOwnUser);
        assertThat(adminUser.getUsername()).isEqualTo(ETestUser.ADMIN_USER.getEmail());

        long countOfAllRights = Objects.requireNonNull(new JPAQuery<>(em)
                .select(QAppActorRight.appActorRight.uuid.count())
                .from(QAppActorRight.appActorRight)
                .fetchOne());

        assertThat(adminUser.getEffectiveRights()).hasSize((int) countOfAllRights);
    }

    @Test
    @WithTestUser(ETestUser.ADMIN_USER)
    void shouldBeAuthenticated() {
        boolean isAuthenticated = assertOkResponse(AuthenticationController::isAuthenticated);
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    @WithoutAuthentication
    void shouldBeNotAuthenticated() {
        boolean isAuthenticated = assertOkResponse(AuthenticationController::isAuthenticated);
        assertThat(isAuthenticated).isFalse();
    }


    @Test
    void shouldBeAbleToLogoutUser() {

        final LoginResult loginResult = loginUser(ETestUser.BASE_USER_1.getEmail(), "*");

        final AppToken appToken = findAppTokenOrGetNull(loginResult.getToken());

        assertThat(appToken).isNotNull();

        UserLogoutRequest userLogoutRequest = new UserLogoutRequest();
        userLogoutRequest.setToken(loginResult.getToken());

        assertOkResponse(c -> c.userLogout(userLogoutRequest));

        assertThat(findAppTokenOrGetNull(loginResult.getToken())).isNull();
    }

    private AppToken findAppTokenOrGetNull(String token) {
        return new JPAQuery<>(em).select(QAppToken.appToken)
                .from(QAppToken.appToken)
                .where(QAppToken.appToken.tokenValue.eq(token))
                .fetchOne();
    }

    private void assertDefaultLoginValidationErrors(ApiError apiError) {
        ValidationResultDto validationResultByApiError = getValidationResultByApiError(apiError);
        assertThat(validationResultByApiError.getFields())
                .hasSize(2)
                .anyMatch(f -> f.getFieldPath().equals("username"))
                .anyMatch(f -> f.getFieldPath().equals("password"));

        validationResultByApiError.getFields().forEach(this::assertDefaultLoginValidationFieldErrors);
    }

    private void assertDefaultLoginValidationFieldErrors(ValidationFieldErrorDto fieldErrorDto) {
        assertThat(fieldErrorDto.getErrorMessages()).hasSize(1)
                .anyMatch(e -> e.getValidationName().equals("INVALID_CREDENTIALS"))
                .anyMatch(e -> e.getMessage().equals("The combination of email and password is not valid"));
    }


    @SuppressWarnings("all")
    private LoginResult loginUser(String email, String password) {
        UserLoginVM requestBody = new UserLoginVM();
        requestBody.setUsername(email);
        requestBody.setPassword(password);

        return postRequest(getLoginLink(), requestBody, LoginResult.class);
    }

    private ApiError loginUserAndExceptError(String email, String password) {
        UserLoginVM requestBody = new UserLoginVM();
        requestBody.setUsername(email);
        requestBody.setPassword(password);

        return postRequest(getLoginLink(), requestBody, ApiErrorWithJavaExceptionInstance.class, HttpStatus.BAD_REQUEST);
    }

    private String getLoginLink() {
        return ApiLink.of(AuthenticationController.class, c -> c.userLogin(null, null)).getLink();
    }

}
