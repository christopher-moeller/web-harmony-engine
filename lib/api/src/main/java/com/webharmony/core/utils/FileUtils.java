package com.webharmony.core.utils;

import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
public class FileUtils {

    private FileUtils() {

    }

    public static Tuple2<String, byte[]> extractFileWebData(FileWebData fileWebData) {
        final String[] dataFragments = fileWebData.getBase64Content().split(",");
        final String webType = dataFragments[0];
        final String base64Content = dataFragments[1];
        byte[] bytesFromRawFile = Base64.getDecoder().decode(base64Content);
        return Tuple2.of(webType, bytesFromRawFile);
    }

    public static String readResourceAsString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    public static void writeToFile(Path path, byte[] content) {
        Files.write(path, content);
    }

    @SneakyThrows
    public static byte[] readBytesFromFile(Path path) {
        return Files.readAllBytes(path);
    }

}
