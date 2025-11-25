package com.webharmony.core.api.rest.controller.log;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.utils.log.LogWatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/logs")
public class LogController extends AbstractBaseController {

    @GetMapping
    @CoreApiAuthorization(ECoreActorRight.CORE_LOGS_READ)
    public ResponseEntity<List<String>> getAllLogs() {
        return ResponseEntity.ok(LogWatcher.getInstance().getLogs());
    }


}
