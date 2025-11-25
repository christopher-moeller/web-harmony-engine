package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.ApplicationStatusController;
import com.webharmony.core.api.rest.model.view.ApplicationStatusVM;
import com.webharmony.core.configuration.EApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.assertj.core.api.Assertions.assertThat;


class ApplicationStatusControllerTest extends AbstractApiTest<ApplicationStatusController> {


    @Override
    @BeforeEach
    protected void beforeEach(TestInfo testInfo) {
        super.beforeEach(testInfo);
    }

    @Test
    void shouldGetCurrentStatusOk() {
        assertThat(getApplicationVM().getStatus()).isEqualTo(EApplicationStatus.OK);
    }

    @Test
    void shouldChangeStatus() {

        final String customUserMessage = "custom user message";
        final String customTechnicalMessage = "custom technical message";

        ApplicationStatusVM vm = new ApplicationStatusVM();
        vm.setStatus(EApplicationStatus.MAINTENANCE_MODE);
        vm.setUserMessage(customUserMessage);
        vm.setTechnicalMessage(customTechnicalMessage);

        assertOkResponse(c -> c.saveCurrentStatus(vm));
        ApplicationStatusVM applicationVM = getApplicationVM();
        assertThat(applicationVM.getStatus()).isEqualTo(EApplicationStatus.MAINTENANCE_MODE);
        assertThat(applicationVM.getUserMessage()).isEqualTo(customUserMessage);
        assertThat(applicationVM.getTechnicalMessage()).isEqualTo(customTechnicalMessage);
    }

    @Test
    void shouldResetStatus() {

        ApplicationStatusVM vm = new ApplicationStatusVM();
        vm.setUserMessage("technical problems user message");
        vm.setTechnicalMessage("technical problems technical message");
        vm.setStatus(EApplicationStatus.TECHNICAL_PROBLEMS);
        assertOkResponse(c -> c.saveCurrentStatus(vm));

        assertThat(getApplicationVM().getStatus()).isEqualTo(EApplicationStatus.TECHNICAL_PROBLEMS);
    }


    private ApplicationStatusVM getApplicationVM() {
        return assertOkResponse(ApplicationStatusController::getCurrentStatus);
    }
}
