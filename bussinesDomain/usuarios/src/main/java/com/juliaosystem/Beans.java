package com.juliaosystem;

import com.common.lib.api.dtos.request.AuditRequest;
import com.common.lib.api.response.AuditResponse;
import com.common.lib.infraestructure.adapters.primary.AuditImpl;
import com.common.lib.infraestructure.services.secundary.CrudSecundaryService;

import com.common.lib.utils.UserResponses;
import com.common.lib.utils.errors.AbtractError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans<E> {

    @Bean(name = "miUserResponses")
    public UserResponses<E> userResponses() {
        return new UserResponses<>();
    }

//
//    @Bean
//    public AuditImpl audit(CrudSecundaryService<AuditRequest, AuditResponse> auditService) {
//        return new AuditImpl(auditService);
//    }


    @Bean(name = "abstractError")
    public AbtractError abtractError() {
        return new AbtractError();
    }

}
