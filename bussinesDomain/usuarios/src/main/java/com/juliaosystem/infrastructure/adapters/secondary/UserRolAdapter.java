package com.juliaosystem.infrastructure.adapters.secondary;


import com.juliaosystem.infrastructure.entitis.UserRol;
import com.juliaosystem.infrastructure.repository.UserRolRepository;
import com.juliaosystem.infrastructure.services.secondary.UserRolServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRolAdapter implements UserRolServiceInter {


    private final UserRolRepository userRolRepository;


    @Override
    @Transactional
    public List<UserRol> add(List<UserRol> userRol) {
        return userRolRepository.saveAll(userRol);
    }
}
