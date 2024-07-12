package com.juliaosystem.infrastructure.services.secondary;

import com.auth0.jwk.Jwk;
import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.common.lib.infraestructure.services.secundary.CrudSecundaryService;
import com.juliaosystem.infrastructure.entitis.User;
import org.springframework.cache.annotation.Cacheable;


public interface UserServiceInter extends CrudSecundaryService<RegisterUserDTO,RegisterUserDTO> {

   PlantillaResponse<RegisterUserDTO> findByEmail(String email);

    @Cacheable(value = "jwkCache")
    Jwk getJwk() throws Exception;


    PlantillaResponse<User> addUser(RegisterUserDTO registerUserDTO, PlantillaResponse<RegisterUserDTO> register);

    PlantillaResponse<RegisterUserDTO> llenarEntidades(User userSave , RegisterUserDTO registerUserDTO);

}
