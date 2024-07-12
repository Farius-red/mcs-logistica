package com.juliaosystem.infrastructure.adapters.primary;


import com.juliaosystem.infrastructure.entitis.UserRol;
import com.juliaosystem.infrastructure.services.secondary.UserRolServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRolImpl {


    private final UserRolServiceInter userRolServiceInter;

    @Transactional
    public List<UserRol> add(List<UserRol> userRol) {
        return userRolServiceInter.add(userRol);
    }
}
