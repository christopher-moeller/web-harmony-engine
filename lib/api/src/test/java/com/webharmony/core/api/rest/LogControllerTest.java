package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.log.LogController;
import com.webharmony.core.utils.log.JavaLogInterceptorHolder;
import com.webharmony.core.utils.log.LogWatcher;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogControllerTest extends AbstractApiTest<LogController> {

    @Test
    void shouldGetAllLogs() {
        final PrintStream defaultOut = System.out;

        try {
            System.setOut(JavaLogInterceptorHolder.getInstance());
            LogWatcher.getInstance().start();
            System.out.println("Test");

            final List<String> logs = assertOkResponse(LogController::getAllLogs);
            assertThat(logs).isNotEmpty();
            assertThat(logs).contains("Test");
        } finally {
            System.setOut(defaultOut);
        }

    }

}
