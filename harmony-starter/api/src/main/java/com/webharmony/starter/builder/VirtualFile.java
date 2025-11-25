package com.webharmony.starter.builder;

import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Objects;

public abstract class VirtualFile {

    @Getter
    final String name;

    @Getter
    @Setter
    private VirtualDir parent;

    protected VirtualFile(String name) {
        this.name = name;
    }

    public Path getFullPath() {
        if (parent == null) {
            throw new IllegalStateException("Parent directory is not set for the file: " + name);
        } else {
            return parent.getFullPath().resolve(name);
        }
    }

    public abstract String createFileContent(ProjectBuildingContext projectBuildingContext);

    protected String resolveTemplateContent(String content, Tuple2<String, Object>... resolvers) {
        String resolvedContent = content;
        for (Tuple2<String, Object> resolver : resolvers) {
            final String key = resolver.getType1();
            final String value = Objects.toString(resolver.getType2());

            resolvedContent = resolvedContent.replace("{{"+ key +"}}", value);
        }
        return resolvedContent;
    }
}
