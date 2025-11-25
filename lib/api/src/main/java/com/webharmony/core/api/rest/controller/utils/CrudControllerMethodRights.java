package com.webharmony.core.api.rest.controller.utils;

import com.webharmony.core.configuration.security.ApplicationRight;

public record CrudControllerMethodRights(ApplicationRight getAllRight, ApplicationRight getByIdRight,
                                         ApplicationRight createNewRight, ApplicationRight updateRight,
                                         ApplicationRight deleteRight) {

}
