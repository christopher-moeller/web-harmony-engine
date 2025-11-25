package com.webharmony.starter.api.rest.controller;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.starter.ProjectCreationService;
import com.webharmony.starter.api.rest.model.CreateHarmonyProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.Path;

@ApiController("api/starter")
public class StarterController extends AbstractBaseController {

    private final ProjectCreationService projectCreationService;

    public StarterController(ProjectCreationService projectCreationService) {
        this.projectCreationService = projectCreationService;
    }

    @PostMapping
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> createProject(@RequestBody final CreateHarmonyProjectRequest request) {
        projectCreationService.createProject(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("template")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<CreateHarmonyProjectRequest> getTemplate() {
        final CreateHarmonyProjectRequest template = new CreateHarmonyProjectRequest();

        final Path path = Path.of("")
                .toAbsolutePath()
                .getParent()
                .getParent().resolve("my-harmony-project");
        template.setPathToProject(path.toAbsolutePath().toString());
        return ResponseEntity.ok(template);
    }

}
