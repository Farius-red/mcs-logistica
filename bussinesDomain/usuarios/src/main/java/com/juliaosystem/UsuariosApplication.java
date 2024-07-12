package com.juliaosystem;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
@EnableFeignClients
public class UsuariosApplication {

	private static final Logger logger = LoggerFactory.getLogger(UsuariosApplication.class);

	@Value("${keycloak.base-uri}")
	private String keycloakBaseUri;

	@Value("${keycloak.client-id}")
	private String keycloakClientId;
	@Value("${keycloak.client-user}")
	private String keycloakClientUser;
	@Value("${keycloak.client-secret}")
	private String keycloakClientSecret;


    public static void main(String[] args) {

		SpringApplication app = new SpringApplication(UsuariosApplication.class);
		Environment env = app.run(args).getEnvironment();
		if (logger.isInfoEnabled()) {
			logger.info("Perfil activo: {}", env.getProperty("spring.profiles.active"));
		}

	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("springshop-public")
				.packagesToScan("com.juliaosystem")
				.build();
	}


	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				.serverUrl(keycloakBaseUri)
				.realm(keycloakClientId)
				.clientId(keycloakClientId)
				.clientSecret(keycloakClientSecret)
				.username(keycloakClientUser)
				.password(keycloakClientSecret)
				.resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
				.build();
	}
}
