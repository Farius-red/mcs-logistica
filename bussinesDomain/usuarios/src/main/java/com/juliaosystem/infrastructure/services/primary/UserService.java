package com.juliaosystem.infrastructure.services.primary;


import com.common.lib.api.dtos.request.AuditRequest;
import com.common.lib.api.dtos.request.LoginDTO;
import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.adapters.primary.UserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {


    private final UserImpl userImpl;


    public Optional<PlantillaResponse<RegisterUserDTO>> login(LoginDTO loginDTO){
        return userImpl.login(loginDTO);
    }

    public void checkValidity(String authHeader) {
        userImpl.checkValidity(authHeader);
    }

    public void logout(String refreshToken)  {
        userImpl.logout(refreshToken);
    }

    public String refresh(String refreshToken)  {
       return  userImpl.refresh(refreshToken);
    }

    public Map<String, Integer> getRoles(String authHeader) throws Exception {
       return  userImpl.getRoles(authHeader);
    }

    public PlantillaResponse<RegisterUserDTO> add(RegisterUserDTO registerUserDTO ,AuditRequest auditRequest) {
       return userImpl.add(registerUserDTO);
    }

    public PlantillaResponse<RegisterUserDTO> getUsers(UUID id, long idBussines , AuditRequest auditRequest) {
        return userImpl.all(id,idBussines);
    }
}
