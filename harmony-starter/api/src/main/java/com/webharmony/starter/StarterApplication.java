package com.webharmony.starter;

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
public class StarterApplication extends AbstractAppMain {

	public static void main(String[] args) {
		StarterApplication starterApplication = new StarterApplication();

		starterApplication
				.prepare()
				.registerApiAuthorizationAnnotation(StarterApiAuthorization.class, a -> ApplicationAccessRule.ofUnrestricted())
				.run(args);
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface StarterApiAuthorization{

		EStarterActorRights[] value();
		boolean isOrConnected() default false;

	}

	public enum EStarterActorRights implements PersistenceEnum<AppActorRight>, ApplicationRight {

		STARTER_TEST_RIGHT("Starter Test Right", "Starter Test Right"),

		;

		@Getter
		private final String label;
		private final String description;

		EStarterActorRights(String label, String description) {
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
