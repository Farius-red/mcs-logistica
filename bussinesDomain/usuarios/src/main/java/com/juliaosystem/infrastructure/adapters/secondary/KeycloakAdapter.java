package com.juliaosystem.infrastructure.adapters.secondary;

import com.common.lib.api.dtos.user.KeycloakBuilderDTO;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdapter {
    @Value("${keycloak.base-uri}")
    private String keycloakBaseUri;
    @Value("${keycloak.client-user}")
    private String keycloakClientUser;
    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;
    public RealmResource realmResource(KeycloakBuilderDTO keycloakBuilderDTO) {
        Keycloak keycloak=   KeycloakBuilder.builder()
                .serverUrl(keycloakBaseUri)
                .realm(keycloakBuilderDTO.getRealm())
                .clientId(keycloakBuilderDTO.getClientId())
                .clientSecret(keycloakClientSecret)
                .username(keycloakClientUser)
                .password(keycloakClientSecret)
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();
        return keycloak.realm(keycloakBuilderDTO.getRealm());
    }
}
