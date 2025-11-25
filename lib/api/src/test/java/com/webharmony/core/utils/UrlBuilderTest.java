package com.webharmony.core.utils;

import com.webharmony.core.AbstractBaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlBuilderTest extends AbstractBaseTest {


    @Test
    void testUrlBuilder1() {
        final String url = UrlBuilder.ofDomain("moeller.de").build();
        assertThat(url).isEqualTo("https://moeller.de");
    }

    @Test
    void testUrlBuilder2() {
        final String url = UrlBuilder.ofDomain("moeller.de/").build();
        assertThat(url).isEqualTo("https://moeller.de");
    }

    @Test
    void testUrlBuilder3() {
        final String url = UrlBuilder.ofDomain("moeller.de").withPath("eins/zwei").build();
        assertThat(url).isEqualTo("https://moeller.de/eins/zwei");
    }

    @Test
    void testUrlBuilder4() {
        final String url = UrlBuilder.ofDomain("moeller.de/").withPath("/eins/zwei/").build();
        assertThat(url).isEqualTo("https://moeller.de/eins/zwei");
    }

    @Test
    void testUrlBuilder5() {
        final String url = UrlBuilder.ofDomain("moeller.de/").withPath("/eins/zwei/").withQuery("q1", 1).withQuery("q2", 2).build();
        assertThat(url).isEqualTo("https://moeller.de/eins/zwei?q1=1&q2=2");
    }

    @Test
    void testUrlBuilder6() {
        final String url = UrlBuilder.ofDomain("moeller.de/").withPort(8080).withPath("/eins/zwei/").withQuery("q1", 1).withQuery("q2", 2).build();
        assertThat(url).isEqualTo("https://moeller.de:8080/eins/zwei?q1=1&q2=2");
    }

    @Test
    void testUrlBuilder7() {
        final String url = UrlBuilder.ofDomain("localhost").withSsl(false).withPort(8080).withPath("/eins/zwei/").withQuery("q1", 1).withQuery("q2", 2).build();
        assertThat(url).isEqualTo("http://localhost:8080/eins/zwei?q1=1&q2=2");
    }


    @Test
    void testUrlBuilder8() {
        final String url = UrlBuilder.ofDomain("http://localhost:8080").withPath("/eins/zwei/").withQuery("q1", 1).withQuery("q2", 2).build();
        assertThat(url).isEqualTo("http://localhost:8080/eins/zwei?q1=1&q2=2");
    }


    @Test
    void testUrlBuilder9() {
        final String url = UrlBuilder.ofDomain("http://localhost:8080").withPath("/eins/zwei/").withQuery("q1", 1).withQuery("q2", 2).build();
        assertThat(url).isEqualTo("http://localhost:8080/eins/zwei?q1=1&q2=2");
    }
}
