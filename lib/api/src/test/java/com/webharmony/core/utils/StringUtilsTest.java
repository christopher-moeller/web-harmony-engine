package com.webharmony.core.utils;

import com.webharmony.core.AbstractBaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest extends AbstractBaseTest {

    @Test
    void shouldConvertJavaStringToHtmlString() {

        final String javaText = "First line \n second line \n\n third line";
        final String result = StringUtils.convertToHtmlString(javaText);
        assertThat(result).isEqualTo("First line <br> second line <br><br> third line");

    }

}
