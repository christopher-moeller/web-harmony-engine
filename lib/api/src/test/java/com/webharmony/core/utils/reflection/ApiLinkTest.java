package com.webharmony.core.utils.reflection;

import com.webharmony.core.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLinkTest extends AbstractBaseTest {


    @Test
    void shouldBeAbleToGenerateDeleteLink() {
        ApiLink apiLink = ApiLink.of(TestControllerWithoutGenericClass.class, c -> c.deleteSomething(null));
        assertThat(apiLink.getRequestMethod()).isEqualTo(RequestMethod.DELETE);
        assertThat(apiLink.getLink()).isEqualTo("/test/delete");

    }

    @RequestMapping("test")
    public static class TestControllerWithoutGenericClass {

        @DeleteMapping("delete")
        @SuppressWarnings("unused")
        public ResponseEntity<?> deleteSomething(@RequestParam(required = false) String testId) {
            return ResponseEntity.ok().build();
        }
    }

}
