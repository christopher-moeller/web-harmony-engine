package com.webharmony.core.utils.reflection.classcontext;

import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ClassContextHolder {

    private final Set<ClassContextEntry> classContextEntries;

    private static ClassContextHolder instance = null;

    public ClassContextHolder(Set<ClassContextEntry> classContextEntries) {
        this.classContextEntries = classContextEntries;
    }

    public static ClassContextHolder getInstance() {
        Assert.isNotNull(instance).withException(() -> new InternalServerException("Instance is null. Pleas call init(...) first")).verify();
        return instance;
    }

    public static void init(String... basePackages) {
        Set<ClassContextEntry> classContextEntries = createEntries(basePackages);
        instance = new ClassContextHolder(classContextEntries);
    }

    private static Set<ClassContextEntry> createEntries(String[] basePackages) {
        Set<ClassContextEntry> entries = new HashSet<>();
        for (String basePackage : basePackages) {
            getAllClassesByPackage(basePackage).stream()
                    .map(ClassContextHolder::createEntryByClass)
                    .forEach(entries::add);
        }
        return entries;
    }

    @SneakyThrows
    private static Set<Class<?>> getAllClassesByPackage(String basePackage) {

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + '/' + "**/*.class";

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] classResources = resolver.getResources(packageSearchPath);

        final Set<Class<?>> resultSet = new HashSet<>();
        for (Resource classAsResource : classResources) {
            try (InputStream is = classAsResource.getInputStream()) {
                final String className = new ClassReader(is).getClassName().replace("/", ".");
                resultSet.add(ReflectionUtils.createClassForName(className));
            }
        }

        log.info("Found {} classes in base package '{}'", resultSet.size(), basePackage);
        return resultSet;
    }

    private static ClassContextEntry createEntryByClass(Class<?> clazz) {
        ClassContextEntry entry = new ClassContextEntry();

        entry.setClassPackage(clazz.getPackage());
        entry.setClazz(clazz);

        entry.setPackageName(clazz.getPackageName());
        entry.setClassName(clazz.getName());

        Set<Class<? extends Annotation>> annotations = ReflectionUtils.getAnnotationTypesOfClass(clazz);
        entry.setAnnotations(annotations);
        entry.setAnnotationNames(annotations.stream().map(Class::getName).collect(Collectors.toSet()));

        return entry;
    }
}
