package com.webharmony.core.utils.dev.i18n;

import com.webharmony.core.i18n.EI18nCodeLocation;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nStaticTranslations;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.RegexUtils;
import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.LocalDevProperties;
import com.webharmony.core.utils.dev.fepages.json.PageJson;
import com.webharmony.core.utils.dev.fepages.json.RouterPagesRoot;
import com.webharmony.core.utils.dev.projectconfig.FrontendProjectConfiguration;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class I18nIndexBuilder {

    private static final String PROJECT_CONFIGURATION_JSON_FILE = "project-configuration.json";

    @SuppressWarnings("java:S4968")
    private final List<Class<? extends I18nStaticTranslations>> staticTranslationEnums = new ArrayList<>();

    public final List<I18nClassDto> buildStaticKeys() {
        initStaticTranslationEnums();
        final LocalDevProperties localDevProperties = DevUtils.loadLocalDevProperties().orElseThrow();

        final List<I18nClassDto> backendClasses = buildI18nClassDTOsForBackend();

        final List<I18nClassDto> frontendClasses = new ArrayList<>();
        frontendClasses.addAll(buildClassDTOsForStaticFrontendTranslations(localDevProperties.getFrontendCoreDevPath(), true));
        frontendClasses.addAll(buildClassDTOsForStaticFrontendTranslations(localDevProperties.getFrontendProjectDevPath(), false));

        final List<I18nClassDto> classes = new ArrayList<>();
        classes.addAll(backendClasses);
        classes.addAll(frontendClasses);
        return classes;
    }

    private void initStaticTranslationEnums() {
        staticTranslationEnums.clear();
        staticTranslationEnums.addAll(ReflectionUtils.getAllProjectClassesImplementingSuperClass(I18nStaticTranslations.class));
    }

    @SneakyThrows
    public void writeJsonFiles(List<I18nClassDto> classes) {

        final List<I18nClassDto> coreClasses = classes.stream().filter(I18nClassDto::getIsCoreClass).toList();
        final List<I18nClassDto> projectClasses = classes.stream().filter(c -> !c.getIsCoreClass()).toList();

        writeJsonFile(coreClasses, true);
        writeJsonFile(projectClasses, false);
    }

    @SneakyThrows
    private void writeJsonFile(List<I18nClassDto> classes, boolean isCore) {

        final Path rootPath = isCore ? DevUtils.getPathToCoreRootFolder() : DevUtils.getPathToProjectRootFolder();
        final Path pathToI18nFolder = rootPath.toAbsolutePath()
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("i18n");

        if(!pathToI18nFolder.toFile().exists() && !pathToI18nFolder.toFile().mkdirs()) {
            throw new IllegalStateException("Cannot create folder: "+pathToI18nFolder);
        }

        final String filename = isCore ? "i18n_static_core_keys.json" : "i18n_static_project_keys.json";
        final Path pathToI18nFile = pathToI18nFolder.resolve(filename);

        JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(pathToI18nFile.toFile(), classes);
    }

    protected List<I18nClassDto> buildI18nClassDTOsForBackend() {
        List<I18nTranslationClassEntry> translationClassEntries = createTranslationClassEntries();
        final List<I18nClassDto> classDTOs = new ArrayList<>();

        int entryCount = 0;
        for (I18nTranslationClassEntry translationClassEntry : translationClassEntries) {

            final I18nClassDto i18nClassDto = new I18nClassDto();
            i18nClassDto.setLocation(EI18nCodeLocation.BACKEND);
            i18nClassDto.setClassId(translationClassEntry.getClazz().getName());

            final String fileAsString = convertFileToString(translationClassEntry.getFileOfClass());

            final String cleanedFile = cleanFileContent(fileAsString);
            final String searchPrefix = translationClassEntry.getInstanceFieldName() + "." + "translate";
            final String searchSuffix = "build";

            final List<String> baseMethodFragments = RegexUtils.getStringsSurroundedBy(cleanedFile, searchPrefix, searchSuffix);

            boolean isCoreClass = ReflectionUtils.isCoreClass(translationClassEntry.getClazz());
            i18nClassDto.setIsCoreClass(isCoreClass);
            final String codeLocationForValue = buildCodeFilePathForBackend( isCoreClass, translationClassEntry.getFileOfClass());
            final List<I18nKeyDto> keys = buildI18nKeysByBaseMethodFragments(baseMethodFragments, translationClassEntry, fileAsString, codeLocationForValue);
            keys.forEach(k -> k.setIsCoreEntry(isCoreClass));
            i18nClassDto.setKeys(keys);

            entryCount = entryCount + keys.size();

            classDTOs.add(i18nClassDto);
        }

        classDTOs.addAll(buildClassDTOsForStaticBackendTranslations());

        log.info("Found {} backend key entries", entryCount);
        return classDTOs;
    }

    private List<I18nKeyDto> buildI18nKeysByBaseMethodFragments(List<String> baseMethodFragments, I18nTranslationClassEntry translationClassEntry, String rawFileContent, String codeFilePath) {
        final List<I18nKeyDto> keys = new ArrayList<>();
        for (String baseMethodFragment : baseMethodFragments) {

            final I18nKeyDto key = new I18nKeyDto();

            final String methodInput = RegexUtils.getStringsSurroundedBy(baseMethodFragment, "(", ")")
                    .iterator()
                    .next();

            if(!methodInput.startsWith("\"") || !methodInput.endsWith("\""))
                throw new IllegalStateException(String.format("Invalid method input in class '%s': '%s'", translationClassEntry.getClazz().getName(), methodInput));

            final String englishDefaultValue = methodInput.replace("\"", "");
            final String keyId = I18N.createKeyIdByEnglishDefaultValue(englishDefaultValue);

            key.setId(keyId);
            key.setEnglishDefaultValue(englishDefaultValue);

            final List<String> placeholderKeys = buildPlaceholders(baseMethodFragment, translationClassEntry.getClass().getName());

            key.setPlaceholders(placeholderKeys);
            key.setCodeLocations(findCodeLocationForValue(englishDefaultValue, rawFileContent, codeFilePath));


            if(keys.stream().noneMatch(k -> k.getId().equals(key.getId()))) {
                keys.add(key);
            }

        }

        return keys;
    }

    private List<String> buildPlaceholders(String baseMethodFragment, String classId) {
        final List<String> resultList = new ArrayList<>();
        final List<String> placeholderFragments = RegexUtils.getStringsSurroundedBy(baseMethodFragment, "add\\(", ")");
        for (String placeholderFragment : placeholderFragments) {
            final String cleanFragment = placeholderFragment.replace("(", "");
            final String rawKey = extractRawKey(classId, cleanFragment);

            resultList.add(rawKey.replace("\"", "").replace("'", ""));
        }
        
        return resultList;
    }

    private static String extractRawKey(String classId, String cleanFragment) {
        final String[] rawKeyValuePair = cleanFragment.split(",");
        if(rawKeyValuePair.length != 2) {
            throw new IllegalStateException(String.format("Invalid placeholder state in class '%s': %s", classId, cleanFragment));
        }

        final String rawKey = rawKeyValuePair[0];
        if((!rawKey.startsWith("\"") || !rawKey.endsWith("\"")) && (!rawKey.startsWith("'") || !rawKey.endsWith("'")))
            throw new IllegalStateException(String.format("Invalid method input for placeholders in class '%s': '%s'", classId, rawKey));
        return rawKey;
    }

    private String cleanFileContent(String fileAsString) {
        return fileAsString.replace("\n", "")
                .replace("\t", "");
    }


    @SneakyThrows
    private static String convertFileToString(Path path) {
        return new String(Files.readAllBytes(path));
    }

    private List<I18nTranslationClassEntry> createTranslationClassEntries() {

        final List<I18nTranslationClassEntry> resultList = new ArrayList<>();
        final Set<Class<? extends I18nTranslation>> translationClasses = ReflectionUtils.getAllProjectClassesImplementingSuperClass(I18nTranslation.class);
        for (Class<? extends I18nTranslation> translationClass : translationClasses) {
            final String fieldName = getFieldNameByTranslationClass(translationClass);

            if(fieldName == null)
                continue;

            I18nTranslationClassEntry entry = new I18nTranslationClassEntry();
            entry.setClazz(translationClass);
            entry.setInstanceFieldName(fieldName);
            entry.setFileOfClass(createFileOfTranslationClass(translationClass));
            resultList.add(entry);
        }

        return resultList;
    }


    private String getFieldNameByTranslationClass(Class<? extends I18nTranslation> translationClass) {
        final List<String> foundFields = new ArrayList<>();
        for (Field declaredField : translationClass.getDeclaredFields()) {
            if(declaredField.getType().equals(I18N.class)) {
                foundFields.add(declaredField.getName());
            }
        }

        if(foundFields.isEmpty()) {
            return null;
        }

        if(foundFields.size() > 1) {
            throw new IllegalStateException(String.format("More than one I18n instance as field found in class '%s': %s", translationClass.getName(), String.join(", ", foundFields)));
        }


        return foundFields.iterator().next();
    }

    private Path createFileOfTranslationClass(Class<?> translationClass) {
        final Path basePath = buildBasePathForClass(translationClass);
        final String path = translationClass.getPackage().getName().replace(".","/");
        final String fileName = translationClass.getSimpleName() + ".java";
        final Path resultFile =  basePath.resolve(path).resolve(fileName);

        if(!resultFile.toFile().exists()) {
            throw new IllegalStateException(String.format("File '%s' does not exist", resultFile.toAbsolutePath()));
        }

        return resultFile;
    }

    private Path buildBasePathForClass(Class<?> clazz) {
        final boolean isTestClass = clazz.getSimpleName().contains("Test");
        final String basePathOfCompiledClass = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String basePathOfModule = basePathOfCompiledClass.replace("/target/classes", "")
                .replace("/target/test-classes", "");
        return Path.of(basePathOfModule).resolve("src")
                .resolve(isTestClass ? "test" : "main")
                .resolve("java");
    }

    @SuppressWarnings("java:S4968")
    private List<I18nClassDto> buildClassDTOsForStaticBackendTranslations() {

        final Map<Class<?>, Set<I18nStaticTranslations>> classTranslationMap = new HashMap<>();

        for (Class<? extends I18nStaticTranslations> staticTranslationEnum : this.staticTranslationEnums) {
            for (I18nStaticTranslations enumConstant : staticTranslationEnum.getEnumConstants()) {
                final Class<?> callingClass = enumConstant.getCallingClass();
                classTranslationMap.computeIfAbsent(callingClass, k -> new HashSet<>());
                classTranslationMap.get(callingClass).add(enumConstant);
            }
        }

        final List<I18nClassDto> resultList = new ArrayList<>();
        for (Map.Entry<Class<?>, Set<I18nStaticTranslations>> classSetEntry : classTranslationMap.entrySet()) {

            final Path fileOfTranslationClass = createFileOfTranslationClass(classSetEntry.getKey());
            final String fileAsString = convertFileToString(fileOfTranslationClass);

            final String codeLocationForValue = buildCodeFilePathForBackend(ReflectionUtils.isCoreClass(classSetEntry.getKey()), fileOfTranslationClass);

            final I18nClassDto i18nClassDto = new I18nClassDto();
            i18nClassDto.setLocation(EI18nCodeLocation.BACKEND);
            i18nClassDto.setClassId(classSetEntry.getKey().getName());

            final List<I18nKeyDto> keyDTOs = new ArrayList<>();
            boolean isCoreEntry = ReflectionUtils.isCoreClass(classSetEntry.getKey());
            for (I18nStaticTranslations staticTranslation : classSetEntry.getValue()) {
                I18nKeyDto keyDto = new I18nKeyDto();
                keyDto.setIsCoreEntry(isCoreEntry);
                keyDto.setId(staticTranslation.getKeyId());
                keyDto.setPlaceholders(staticTranslation.getPlaceholders());
                keyDto.setEnglishDefaultValue(staticTranslation.getDefaultValue());
                keyDto.setPlaceholders(findCodeLocationForValue(String.format("%s.%s", staticTranslation.getClass().getSimpleName(), staticTranslation.name()), fileAsString, codeLocationForValue));

                keyDTOs.add(keyDto);
            }

            i18nClassDto.setKeys(keyDTOs);
            resultList.add(i18nClassDto);
        }

        return resultList;
    }

    @SneakyThrows
    public List<I18nClassDto> buildClassDTOsForStaticFrontendTranslations(String feLocationPath, boolean isCore) {
        final File frontendSrcFolder = new File(feLocationPath);

        final Map<String, List<I18nKeyDto>> keyMap = new HashMap<>();

        int countOfKeys = 0;
        try (Stream<Path> stream = Files.walk(Paths.get(frontendSrcFolder.getAbsolutePath()))) {
            List<Path> paths = stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        final String absolutePath = p.toAbsolutePath().toString();
                        return !absolutePath.contains("node_modules") && !absolutePath.contains(".nuxt") && !absolutePath.contains(".output") && !absolutePath.contains("I18N.ts");
                    })
                    .toList();

            final Map<String, Set<Path>> i18nKeyPathsMap = new HashMap<>();
            for (Path path : paths) {
                final String rawFileContent = convertFileToString(path);
                final String cleanFileContent = cleanFileContent(rawFileContent);
                final String fallbackClassId = buildFallbackClassIdOrNull(cleanFileContent, path);
                final List<String> baseMethodFragments = buildBaseMethodFragmentsForFrontendClass(cleanFileContent);

                for (Tuple2<String, I18nKeyDto> tuple : buildI18nFrontendKeys(baseMethodFragments, fallbackClassId, isCore, rawFileContent, buildCodeFilePathForFrontend(feLocationPath, isCore, path))) {
                    i18nKeyPathsMap.computeIfAbsent(tuple.getType1(), c -> new HashSet<>()).add(path);

                    keyMap.computeIfAbsent(tuple.getType1(), c -> new ArrayList<>());
                    keyMap.get(tuple.getType1()).add(tuple.getType2());
                    countOfKeys++;
                }
            }

            for (Map.Entry<String, Set<Path>> entry : i18nKeyPathsMap.entrySet()) {
                final String i18nKey = entry.getKey();
                final Set<Path> values = entry.getValue();
                if(values.size() > 1) {
                    log.error(String.format("Frontend I18nKey '%s' is used in multiple files: %s", i18nKey, values.stream().map(Path::toString).toList()));
                }
            }
        }

        if(!isCore) {
            final List<I18nKeyDto> routerPagesJsonEntries = getRouterPagesJsonEntries(frontendSrcFolder.toPath());
            if(!routerPagesJsonEntries.isEmpty()) {
                keyMap.put(PROJECT_CONFIGURATION_JSON_FILE, routerPagesJsonEntries);
                countOfKeys += routerPagesJsonEntries.size();
            }
        }
        log.info("Found {} frontend key entries", countOfKeys);

        final List<I18nClassDto> resultList = new ArrayList<>();
        for (Map.Entry<String, List<I18nKeyDto>> entry : keyMap.entrySet()) {
            final I18nClassDto classDto = new I18nClassDto();
            classDto.setIsCoreClass(isCore);
            classDto.setLocation(EI18nCodeLocation.FRONTEND);
            classDto.setClassId(entry.getKey());
            classDto.setKeys(entry.getValue());
            resultList.add(classDto);
        }

        return resultList;
    }

    @SneakyThrows
    private List<I18nKeyDto> getRouterPagesJsonEntries(Path frontendPath) {
        final Path projectConfigurationPath = frontendPath.resolve(PROJECT_CONFIGURATION_JSON_FILE);
        final File routerPagesFile = projectConfigurationPath.toFile();
        if(!routerPagesFile.exists())
            return Collections.emptyList();

        final String rawFileContent = convertFileToString(projectConfigurationPath);
        final String codeFilePath = buildCodeFilePathForFrontend(frontendPath.toAbsolutePath().toString(), false, projectConfigurationPath);

        final List<I18nKeyDto> resultList = new ArrayList<>();

        final RouterPagesRoot routerPagesRoot = JacksonUtils.createDefaultJsonMapper().readValue(routerPagesFile, FrontendProjectConfiguration.class)
                .getRouterPages();

        for (PageJson page : routerPagesRoot.getPages()) {

            final String englishValue = page.getEnglishLabel();

            final String keyId = I18N.createKeyIdByEnglishDefaultValue(englishValue);

            if(resultList.stream().anyMatch(key -> key.getId().equals(keyId))) {
                continue;
            }

            final I18nKeyDto keyDto = new I18nKeyDto();
            keyDto.setIsCoreEntry(false);
            keyDto.setId(keyId);
            keyDto.setEnglishDefaultValue(englishValue);
            keyDto.setCodeLocations(findCodeLocationForValue(englishValue, rawFileContent, codeFilePath));
            keyDto.setPlaceholders(Collections.emptyList());
            resultList.add(keyDto);
        }
        return resultList;
    }

    private String buildCodeFilePathForFrontend(String frontendLocationPath, boolean isCore, Path path) {
        final String cleanPath = path.toString().replace(frontendLocationPath, "");
        final String prefix = isCore ? "%FE_CORE%" : "%FE_PROJECT%";
        return prefix + cleanPath;
    }

    private String buildCodeFilePathForBackend(boolean isCore, Path path) {
        final String backendLocationPath = isCore ? DevUtils.getPathToCoreRootFolder().toString() : DevUtils.getPathToProjectRootFolder().toString();
        final String cleanPath = path.toString().replace(backendLocationPath, "");
        final String prefix = isCore ? "%BE_CORE%" : "%BE_PROJECT%";
        return prefix + cleanPath;
    }

    private String buildFallbackClassIdOrNull(String cleanFileContent, Path path) {
        if(cleanFileContent.contains("I18N.of('")) {
            throw new IllegalStateException("Invalid use of I18N.of('...'). Pleas use double quotes in file "+path.toAbsolutePath());
        }
        List<String> stringsSurroundedBy = RegexUtils.getStringsSurroundedBy(cleanFileContent, "I18N.of", "\"\\)");
        if(!stringsSurroundedBy.isEmpty()) {
            return stringsSurroundedBy.get(0).replace("I18N.of(\"", "").replace("\")", "");
        }
        return null;
    }

    private List<String> buildBaseMethodFragmentsForFrontendClass(String cleanFileContent) {
        final String searchSuffix = "build";

        final List<String> baseMethodFragments = new ArrayList<>();
        for (String varName : List.of("i18n", "I18N", "i18N")) {
            final String searchPrefix = String.format("%s.translate", varName);
            baseMethodFragments.addAll(RegexUtils.getStringsSurroundedBy(cleanFileContent, searchPrefix, searchSuffix));
        }

        return baseMethodFragments;
    }

    public List<Tuple2<String, I18nKeyDto>> buildI18nFrontendKeys(List<String> baseMethodFragments, String fallbackClassId, boolean isCoreFile, String rawFileContent, String codeFilePath) {
        final List<Tuple2<String, I18nKeyDto>> resultList = new ArrayList<>();
        for (String baseMethodFragment : baseMethodFragments) {
            final String methodInput = RegexUtils.getStringsSurroundedBy(baseMethodFragment, "(", ")")
                    .iterator()
                    .next();
            
            final List<String> methodInputFragments = Stream.of(methodInput.split("\",\"")).map(e -> e.replace("\"", "")).toList();
            boolean hasOneEntry = methodInputFragments.size() == 1;

            final String classId = hasOneEntry ? fallbackClassId : methodInputFragments.get(0);
            String englishDefaultValue = hasOneEntry ? methodInputFragments.get(0) : methodInputFragments.get(1);

            if(englishDefaultValue.startsWith("'"))
                englishDefaultValue = englishDefaultValue.substring(1);

            if(englishDefaultValue.endsWith("'"))
                englishDefaultValue = englishDefaultValue.substring(0, englishDefaultValue.length() - 1);

            final String keyId = I18N.createKeyIdByEnglishDefaultValue(englishDefaultValue);

            final I18nKeyDto keyDto = new I18nKeyDto();
            keyDto.setIsCoreEntry(isCoreFile);
            keyDto.setId(keyId);
            keyDto.setEnglishDefaultValue(englishDefaultValue);
            keyDto.setCodeLocations(findCodeLocationForValue(englishDefaultValue, rawFileContent, codeFilePath));
            keyDto.setPlaceholders(buildPlaceholders(baseMethodFragment, classId));

            if(resultList.stream().noneMatch(e -> e.getType2().getId().equals(keyDto.getId()))) {
                resultList.add(Tuple2.of(classId, keyDto));
            }
        }

        return resultList;
    }

    private List<String> findCodeLocationForValue(String englishDefaultValue, String rawFileContent, String codeFilePath) {
        final List<String> resultList = new ArrayList<>();
        final String[] lines = rawFileContent.split("\n");
        for(int i=0; i<lines.length; i++) {
            String currentLine = lines[i];

            int columnNumber = currentLine.indexOf(englishDefaultValue);
            if(columnNumber != -1) {
                int lineNumber = i+1;
                resultList.add(String.format("%s:%s:%s", codeFilePath, lineNumber, columnNumber));
            }
        }
        return resultList;
    }

    @Getter
    @Setter
    private static class I18nTranslationClassEntry {

        private Class<? extends I18nTranslation> clazz;
        private String instanceFieldName;
        private Path fileOfClass;

    }

}
