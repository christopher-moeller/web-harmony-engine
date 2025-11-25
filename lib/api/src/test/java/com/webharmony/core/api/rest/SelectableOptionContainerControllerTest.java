package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.SelectableOptionContainerController;
import com.webharmony.core.data.enums.ECoreActorRight;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectableOptionContainerControllerTest extends AbstractApiTest<SelectableOptionContainerController> {

    @Test
    void shouldGetAllOptionContainers() {
        List<String> optionContainers = assertOkResponse(SelectableOptionContainerController::getOptionContainers);
        assertThat(optionContainers).isNotEmpty();
    }

    @Test
    void shouldGetSelectableRightsApiObjectOptionContainer() {
        List<Object> options = assertOkResponse(c -> c.getOptionsByContainer("SelectableRightsApiObjectOptionContainer", true));
        assertThat(options).hasSameSizeAs(ECoreActorRight.values());
    }
}
