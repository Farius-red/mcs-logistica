package com.juliaosystem.api.mappers;

import com.common.lib.api.dtos.user.RolDTO;
import com.common.lib.infraestructure.entitis.Roles;
import com.juliaosystem.infrastructure.entitis.Roles;
import com.common.lib.utils.enums.RolEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class RolMapper implements PlantillaMapers<Roles,D> {


    @Override
    public List<D> getListDTO(List<Roles> t) {
        List<D> rolDTOS = new ArrayList<D>();
        t.forEach(rol -> rolDTOS.add(getDTO(rol)));
        return rolDTOS;
    }

    @Override
    public D getDTO(Roles rol) {
        return RolDTO.builder()
                .idRol(rol.getIdRol())
                .nameRol(rol.getNameRol().name())
                .build();
    }

    @Override
    public List<Roles> getListEntyti(List<D> d) {
        List<Roles> rols = new ArrayList<>();
        d.forEach(rolDTO -> rols.add(getEntyti(rolDTO)));
        return rols;
    }

    @Override
    public Roles getEntyti(D rolDTO) {
        return Roles.builder()
                .idRol(rolDTO.getIdRol())
                .nameRol(RolEnum.valueOf(rolDTO.getNameRol()))
                .build();
    }
}
