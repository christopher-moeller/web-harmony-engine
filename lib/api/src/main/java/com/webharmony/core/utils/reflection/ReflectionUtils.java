package com.webharmony.core.utils.reflection;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.EJsonType;
import com.webharmony.core.api.rest.model.utils.SimpleFieldTypeSchema;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiObject;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.validinput.*;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.ReflectionException;
import com.webharmony.core.utils.reflection.classcontext.ClassContextEntry;
import com.webharmony.core.utils.reflection.classcontext.ClassContextHolder;
import com.webharmony.core.utils.reflection.proxy.ProxyExecutionMethod;
import com.webharmony.core.utils.reflection.proxy.ProxyMethodResult;
import com.webharmony.core.utils.reflection.proxy.ProxyUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectionUtils {

    private ReflectionUtils() {

    }

    private static final String GETTER_METHOD_PREFIX = "get";
    private static final String SETTER_METHOD_PREFIX = "set";
    private static final String IS_METHOD_PREFIX = "is";

    @SneakyThrows
    public static Method getMostSpecificMethodByClass(Class<?> clazz, Method method) {
        return clazz.getMethod(method.getName(), method.getParameterTypes());
    }

    public static <C> Method getMethodByClass(Class<C> clazz, ProxyExecutionMethod<C, ?> method) {
        ProxyMethodResult result = ProxyUtils.proxyClazz(clazz).withMethod(method).build();
        return result.getMethod();
    }

    public static Optional<Method> findGetterByField(Field field, Class<?> clazz) {
        String getterName = GETTER_METHOD_PREFIX + StringUtils.firstLetterToUpperCase(field.getName());
        try {
            return Optional.of(clazz.getMethod(getterName));
        }catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Optional<Method> findSetterByField(Field field, Class<?> clazz) {
        String setterName = SETTER_METHOD_PREFIX + StringUtils.firstLetterToUpperCase(field.getName());
        try {
            return Optional.of(clazz.getMethod(setterName, field.getType()));
        }catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<Class<T>> getGenericTypeByClassOrSuperClassesAndIndex(Class<?> clazz, int index) {
        Optional<Class<T>> result = Optional.empty();
        Class<?> currentClass = clazz;
        while (result.isEmpty() || clazz.equals(Objects.class)) {
            try {
                result = getGenericTypeByClassAndIndex(currentClass, index);
            }catch (Exception ignore) {
                // this exception can be ignored
            } finally {
                currentClass = clazz.getSuperclass();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericTypeByClassAndIndexOrThrow(Class<?> clazz, int index) {
        return (Class<T>) getGenericTypeByClassAndIndex(clazz, index).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Class<T>> getGenericTypeByClassAndIndex(Class<?> clazz, int index) {
        ParameterizedType parameterizedType = getParameterizedType(clazz);
        Type type = parameterizedType.getActualTypeArguments()[index];
        if(type instanceof TypeVariable<?> typeVariable) {
            Type[] boundTypes = typeVariable.getBounds();
            if(boundTypes.length == 1)
                return Optional.of((Class<T>) boundTypes[0]);
        }

        if(type instanceof Class<?>) {
            return Optional.of((Class<T>) type);
        } else {
            return Optional.empty();
        }
    }

    private static ParameterizedType getParameterizedType(Class<?> clazz) {
        Type genericSuperClassType = clazz.getGenericSuperclass();
        if(genericSuperClassType instanceof ParameterizedType parameterizedType)
            return parameterizedType;

        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if(genericInterfaces.length == 0) {
            throw new InternalServerException("no generic interfaces or classes found");
        }

        if(genericInterfaces.length > 1) {
            final List<Class<?>> ignorableInterfaces = List.of(I18nTranslation.class);
            final List<Type> filteredTypes = Arrays.stream(genericInterfaces)
                    .filter(interF -> ignorableInterfaces.stream().noneMatch(ignore -> ignore.equals(interF)))
                    .toList();

            if(filteredTypes.size() > 1) {
                throw new InternalServerException("more than one generic interface found");
            } else {
                return (ParameterizedType) filteredTypes.iterator().next();
            }
        }

        return (ParameterizedType) genericInterfaces[0];
    }

    public static <C> String getFieldNameByGetterMethod(Class<C> clazz, ProxyExecutionMethod<C, ?> method) {
        Method methodInstance = getMethodByClass(clazz, method);
        return getFieldNameByGetterMethod(methodInstance);
    }

    public static String getFieldNameByGetterMethod(Method getterMethod) {
        final String methodName = getterMethod.getName();

        if(!methodName.startsWith(GETTER_METHOD_PREFIX) && !methodName.startsWith(IS_METHOD_PREFIX))
            throw new ReflectionException(String.format("Name of field must start with '%s' or '%s'", GETTER_METHOD_PREFIX, IS_METHOD_PREFIX));

        final String cleanedMethodName;
        if(methodName.startsWith(GETTER_METHOD_PREFIX))
            cleanedMethodName = methodName.replaceFirst(GETTER_METHOD_PREFIX, "");
        else if(methodName.startsWith(IS_METHOD_PREFIX))
            cleanedMethodName = methodName.replaceFirst(IS_METHOD_PREFIX, "");
        else
            cleanedMethodName = methodName;

        return StringUtils.firstLetterToLowerCase(cleanedMethodName);
    }

    @SneakyThrows
    public static Field getFieldByName(Class<?> clazz, String name) {
        return ReflectionUtils.getAllFields(clazz, true).stream()
                .filter(f -> f.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new InternalServerException(String.format("Field '%s' not found on class '%s", name, clazz)));
    }

    public static List<Field> getAllFields(Class<?> type, boolean includingSuperClasses) {
        if(includingSuperClasses) {
            List<Field> allFields = new ArrayList<>();
            return getAllFieldsRecursive(allFields, type);
        } else {
            return Arrays.asList(type.getDeclaredFields());
        }
    }

    private static List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFieldsRecursive(fields, type.getSuperclass());
        }

        return fields;
    }

    public static List<Method> getAllMethods(Class<?> type, boolean includingSuperClasses) {
        if(includingSuperClasses) {
            List<Method> allMethods = new ArrayList<>();
            return getAllMethodsRecursive(allMethods, type);
        } else {
            return Arrays.asList(type.getDeclaredMethods());
        }
    }

    private static List<Method> getAllMethodsRecursive(List<Method> methods, Class<?> type) {
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));

        if(type.getSuperclass() != null) {
            getAllMethodsRecursive(methods, type.getSuperclass());
        }

        return methods;
    }

    public static Set<Class<? extends Annotation>> getAnnotationTypesOfClass(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations()).map(Annotation::annotationType).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getAllProjectClassesImplementingSuperClass(Class<T> superClass) {
        return getAllProjectClasses(entry -> !superClass.equals(entry.getClazz()) && superClass.isAssignableFrom(entry.getClazz()))
                .stream()
                .map(c -> (Class<? extends T>) c)
                .collect(Collectors.toSet());

    }

    public static Set<Class<?>> getAllProjectClasses(Predicate<? super ClassContextEntry> filter) {
        return ClassContextHolder.getInstance().getClassContextEntries()
                .stream()
                .filter(filter)
                .map(ClassContextEntry::getClazz)
                .collect(Collectors.toSet());
    }

    public static Optional<Class<?>> findAssignableClassInTypeOrInTypeOfCollection(Field field, Class<?> targetType) {
        if(targetType.isAssignableFrom(field.getType()))
            return Optional.of(field.getType());
        else if (Collection.class.isAssignableFrom(field.getType())) {
            Type type = field.getGenericType();
            final Class<?> typeClass = getTypeClass(type);
            return Optional.ofNullable(typeClass)
                    .filter(targetType::isAssignableFrom)
                    .map(t -> (Class<?>) t);
        } else {
            return Optional.empty();
        }
    }

    private static Class<?> getTypeClass(Type type) {
        final Class<?> typeClass;
        if(type instanceof ParameterizedType parameterizedType) {
            Type subType = parameterizedType.getActualTypeArguments()[0];
            if(subType instanceof TypeVariable<?> typeVariable) {
                typeClass = (Class<?>) typeVariable.getBounds()[0];
            } else if (subType instanceof ParameterizedType subParameterizedType) {
                typeClass = (Class<?>) subParameterizedType.getRawType();
            } else {
                typeClass = (Class<?>) subType;
            }
        } else {
            typeClass = (Class<?>) type;
        }
        return typeClass;
    }

    public static Map<String, SimpleFieldTypeSchema> createFieldMapForType(Class<?> fieldType) {
        Map<String, SimpleFieldTypeSchema> fieldsMap = new HashMap<>();
        final List<Field> includedFields = new ArrayList<>();
        for(Field field : ReflectionUtils.getAllFields(fieldType, true)) {
            if(includedFields.stream().anyMatch(includedField -> field.getName().equals(includedField.getName()) && field.getDeclaringClass().isAssignableFrom(includedField.getDeclaringClass()))) {
                continue;
            }
            String name = field.getName();
            SimpleFieldTypeSchema fieldSchema = createFieldTypeSchemaByField(field);
            fieldsMap.put(name, fieldSchema);
            includedFields.add(field);
        }
        return fieldsMap;
    }

    private static SimpleFieldTypeSchema createFieldTypeSchemaByField(Field field) {
        Class<?> fieldType = Collection.class.isAssignableFrom(field.getType()) ? getGenericTypeOfCollection(field) : field.getType();
        return createFieldTypeSchemaByField(field, fieldType);
    }

    @SneakyThrows
    private static SimpleFieldTypeSchema createFieldTypeSchemaByField(Field field, Class<?> fieldType) {
        final SimpleFieldTypeSchema schema;
        if(isNestedDtoType(field, fieldType)) {
            ComplexTypeSchema complexTypeschema = new ComplexTypeSchema();
            Map<String, SimpleFieldTypeSchema> fieldsMap = createFieldMapForType(fieldType);
            complexTypeschema.setFields(fieldsMap);
            schema = complexTypeschema;
        } else if(ApiObject.class.isAssignableFrom(field.getType())) {
            Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            ComplexTypeSchema complexTypeschema = new ComplexTypeSchema();
            if(!(genericType instanceof WildcardType)) {
                Class<?> genericTypeAsClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                Field dataField = ReflectionUtils.getAllFields(fieldType, true).stream().filter(f -> f.getName().equals("data")).findAny().orElseThrow();
                complexTypeschema.setFields(Map.of("data", createFieldTypeSchemaByField(dataField, genericTypeAsClass)));
            }
            schema = complexTypeschema;
        } else {
            schema = new SimpleFieldTypeSchema();
        }

        ValidInputSpecification validInputSpecification = getValidInputSpecificationForField(field);
        validInputSpecification.setIsReadOnly(AnnotationUtils.getAnnotation(field, ReadOnlyAttribute.class) != null);
        schema.setValidInputSpecification(validInputSpecification);

        schema.setSimpleType(fieldType.getSimpleName());
        schema.setJavaType(fieldType.getName());
        schema.setJsonType(getJsonTypeByFieldType(field.getType()).name());

        return schema;
    }

    private static boolean isNestedDtoType(Field field, Class<?> fieldType) {
        if(BaseDto.class.isAssignableFrom(fieldType)) {
            return true;
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return BaseDto.class.isAssignableFrom(getGenericTypeOfCollection(field));
        } else {
            return false;
        }
    }

    public static Class<?> getGenericTypeOfCollection(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type type = parameterizedType.getActualTypeArguments()[0];
        return type instanceof ParameterizedType paraType ? (Class<?>) paraType.getRawType() : (Class<?>) type;
    }

    private static EJsonType getJsonTypeByFieldType(Class<?> fieldType) {
        if(BaseDto.class.isAssignableFrom(fieldType))
            return EJsonType.OBJECT;
        else if(Collection.class.isAssignableFrom(fieldType))
            return EJsonType.ARRAY;
        else
            return EJsonType.FIELD;
    }

    @SuppressWarnings("unchecked")
    private static ValidInputSpecification getValidInputSpecificationForField(Field field) {
        Class<?> fieldType = field.getType();
        if(Collection.class.isAssignableFrom(fieldType))
            fieldType = getGenericTypeOfCollection(field);

        if(String.class.isAssignableFrom(fieldType)) {
            return new TextInputSpecification();
        } else if(ApiResource.class.isAssignableFrom(fieldType)) {
            return new ApiLinkResourceSpecification(field);
        } else if(fieldType.isEnum()) {
            return new EnumInputSpecification((Class<? extends Enum<?>>) fieldType);
        } else {
            return new DefaultInputSpecification();
        }
    }

    @SneakyThrows
    public static <T> T createNewInstanceWithEmptyConstructor(@NonNull Class<T> type) {
        return type.getConstructor().newInstance();
    }

    @SneakyThrows
    public static <T> T createNewInstanceWithSingleAttributeConstructor(@NonNull Class<T> type, @NonNull Object attribute) {
        return type.getConstructor(attribute.getClass()).newInstance(attribute);
    }

    public static Field getFieldByPath(Class<?> rootClass, String path) {
        Class<?> currentClass = rootClass;
        Field lastField = null;
        Type[] lastGenericTypes = new Class[0];
        for(String pathFragment : path.split("\\.")) {

            final String currentPathFragment;
            if(pathFragment.contains("[")) {
                currentPathFragment = pathFragment.substring(0, pathFragment.indexOf("["));
                currentClass = getGenericTypeOfCollection(Objects.requireNonNull(lastField));
            } else {
                currentPathFragment = pathFragment;
            }

            lastField = getAllFields(currentClass, true)
                    .stream()
                    .filter(f -> f.getName().equals(currentPathFragment))
                    .findAny()
                    .orElseThrow();

            if(lastField.getGenericType() instanceof ParameterizedType parameterizedType) {
                lastGenericTypes = parameterizedType.getActualTypeArguments();
            }

            if(lastField.getGenericType() instanceof TypeVariable<?>) {
                // this works only for one generic type
                currentClass = (Class<?>) lastGenericTypes[0];
            } else {
                currentClass = lastField.getType();
            }
        }

        return lastField;
    }

    public static <T extends Enum<?>> T getEnumConstant(Class<T> enumClass, String constantName) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().equals(constantName))
                .findAny()
                .orElseThrow();
    }

    public static Set<Class<?>> buildSetNestedOfAllNestedTypes(Class<?> currentSchema) {
        final Set<Class<?>> resultSet = new HashSet<>();

        if (!(isProjectOrCoreClass(currentSchema) && !currentSchema.isEnum()))
            return resultSet;

        resultSet.add(currentSchema);

        Optional.ofNullable(currentSchema.getSuperclass())
                .filter(ReflectionUtils::isProjectOrCoreClass)
                .ifPresent(superClass -> resultSet.addAll(buildSetNestedOfAllNestedTypes(superClass)));

        for (Field declaredField : currentSchema.getDeclaredFields()) {
            final Class<?> fieldType = declaredField.getType();

            if (List.class.equals(fieldType)) {
                final ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
                Type actualTypeArgument = genericType.getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class<?> clazz) {
                    if(!resultSet.contains(clazz)) {
                        resultSet.addAll(buildSetNestedOfAllNestedTypes(clazz));
                    }
                } else if (actualTypeArgument instanceof ParameterizedType parameterizedType) {
                    resultSet.addAll(buildSetNestedOfAllNestedTypes((Class<?>) parameterizedType.getRawType()));
                } else {
                    throw new InternalServerException(String.format("Field type '%s' is not supported", fieldType));
                }
            } else {
                resultSet.addAll(buildSetNestedOfAllNestedTypes(fieldType));
            }
        }

        return resultSet;
    }

    public static boolean isProjectOrCoreClass(Class<?> clazz) {
        return !clazz.isPrimitive() && clazz.getPackage() != null && clazz.getPackage().getName().contains("com.webharmony");
    }

    @SneakyThrows
    public static Class<?> createClassForName(String className) {
        return Class.forName(className);
    }

    public static boolean isCoreClass(Class<?> clazz) {
        return clazz.getName().startsWith("com.webharmony.core");
    }

    public static <T> T getApiLinkMockValue() {
        return null;
    }
}
