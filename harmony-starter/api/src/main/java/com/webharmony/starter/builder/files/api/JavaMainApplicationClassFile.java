package com.webharmony.starter.builder.files.api;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class JavaMainApplicationClassFile extends VirtualFile {

    @SuppressWarnings("all")
    private static final String TEMPLATE = """
            package com.webharmony.{{technicalName}};
                        
            import com.webharmony.core.AbstractAppMain;
            import com.webharmony.core.configuration.security.ApplicationAccessRule;
            import com.webharmony.core.configuration.security.ApplicationRight;
            import com.webharmony.core.data.enums.utils.PersistenceEnum;
            import com.webharmony.core.data.jpa.model.user.AppActorRight;
            import com.webharmony.core.i18n.I18N;
            import lombok.Getter;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            import org.springframework.context.annotation.Configuration;
                        
            import java.lang.annotation.ElementType;
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            import java.lang.annotation.Target;
                        
            @Configuration
            @SpringBootApplication
            public class {{className}} extends AbstractAppMain {
                        
            	public static void main(String[] args) {
            		new {{className}}()
            				.prepare()
            				.registerApiAuthorizationAnnotation({{authorizationAnnotationClassName}}.class, a -> ApplicationAccessRule.of(a.value(), a.isOrConnected()))
            				.run(args);
            	}
                        
                        
            	@Retention(RetentionPolicy.RUNTIME)
            	@Target(ElementType.METHOD)
            	public @interface {{authorizationAnnotationClassName}} {
                        
            		{{rightEnumName}}[] value();
            		boolean isOrConnected() default false;
                        
            	}
                        
            	public enum {{rightEnumName}} implements PersistenceEnum<AppActorRight>, ApplicationRight {
                        
            		DEMO_TEST_RIGHT("Demo Test Right", "Demo Test Right"),
                        
            		;
                        
            		@Getter
            		private final String label;
            		private final String description;
                        
            		{{rightEnumName}}(String label, String description) {
            			this.label = label;
            			this.description = description;
            		}
                        
            		@Override
            		public void initEntity(AppActorRight entity) {
            			entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
            			entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
            		}
                        
            		@Override
            		public Class<AppActorRight> getEntityClass() {
            			return AppActorRight.class;
            		}
            	}
                        
            }
                        
            """;


    private final String className;

    public JavaMainApplicationClassFile(ProjectBuildingContext context) {
        super(String.format("%sApplication.java", StringUtils.firstLetterToUpperCase(context.getTechnicalName())));
        this.className = getName().replace(".java", "");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {

        final String technicalNameWithUpperLetter = StringUtils.firstLetterToUpperCase(projectBuildingContext.getTechnicalName());

        final String authorizationAnnotationClassName = String.format("%sApiAuthorization", technicalNameWithUpperLetter);
        final String rightEnumName = String.format("E%sActorRights", technicalNameWithUpperLetter);

        return resolveTemplateContent(TEMPLATE,
                Tuple2.of("technicalName", projectBuildingContext.getTechnicalName()),
                Tuple2.of("className", className),
                Tuple2.of("authorizationAnnotationClassName", authorizationAnnotationClassName),
                Tuple2.of("rightEnumName", rightEnumName)
        );
    }
}
