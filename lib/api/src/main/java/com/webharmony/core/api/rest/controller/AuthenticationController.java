package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.model.AuthenticatedUserDto;
import com.webharmony.core.api.rest.model.LoginResult;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.api.rest.model.view.user.UserLogoutRequest;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.service.authentication.types.UnknownSystemAuthentication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController("api/authentication")
public class AuthenticationController extends AbstractBaseController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("userLogin")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<LoginResult> userLogin(@RequestBody UserLoginVM body, HttpServletResponse response) {
        LoginResult loginResult = authenticationService.loginUser(body);
        setAuthenticationToken(loginResult, response);
        return ResponseEntity.ok(loginResult);
    }

    @PostMapping("userLogout")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> userLogout(@RequestBody UserLogoutRequest logoutRequest) {
        authenticationService.logoutUser(logoutRequest);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("me")
    @CoreApiAuthorization(ECoreActorRight.CORE_AUTHENTICATION_OWN_ACTOR)
    public ResponseEntity<AuthenticatedUserDto> getOwnUser() {
        return ResponseEntity.ok(authenticationService.getOwnUser());
    }

    @GetMapping("isAuthenticated")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Boolean> isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof UnknownSystemAuthentication);
        return ResponseEntity.ok(isAuthenticated);
    }

    private void setAuthenticationToken(LoginResult loginResult, HttpServletResponse response) {
        if(response != null)
            response.addCookie(new Cookie(loginResult.getTokenHeaderField(), loginResult.getToken()));
    }

}
