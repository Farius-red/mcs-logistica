package com.juliaosystem.infrastructure.services.consumer;



import org.keycloak.representations.idm.CertificateRepresentation;
import org.keycloak.representations.oidc.OIDCClientRepresentation;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;

@FeignClient(name = "keycloak", url = "${keycloak.base-uri}")
public interface KeycloakConsumer {

    @GetMapping("${keycloak.authorization-uri}")
    String getAuthorizationUri();

    @GetMapping("${keycloak.user-info-uri}")
    String getUserInfoUri();

    @PostMapping("${keycloak.token-uri}")
    String getTokenUri();

    @GetMapping("${keycloak.logout}")
    String getLogoutUri(@PathVariable("realm") String realm);



    @GetMapping("${keycloak.add-rol}")
    String getAddRoleUri(@PathVariable("realm") String realm);

    @GetMapping("${keycloak.get-user}")
    String getAllUser(@PathVariable("realm") String realm);

    @DeleteMapping("${keycloak.delete-user}")
    String deleteUserId(@PathVariable("id") String userId);

    @PutMapping("{keycloak.update-user}")
    void putUser(@PathVariable("id") String id, @PathVariable ("realm") String realm , @RequestBody OIDCClientRepresentation userRepresentationDTO);

    @PostMapping("{reset-password-admin}")
    void updatePasswordAdmin(@PathVariable("id") String id, @PathVariable ("realm") String realm , @RequestBody CertificateRepresentation credentialRepresentation);

    @PostMapping("{keycloak.update-own-user}")
    void UpdateOwnUser(@PathVariable("id") String id, @PathVariable ("realm") String realm);
}
