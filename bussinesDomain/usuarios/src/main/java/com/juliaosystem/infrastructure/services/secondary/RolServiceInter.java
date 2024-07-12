package com.juliaosystem.infrastructure.services.secondary;

import com.juliaosystem.infrastructure.entitis.Roles;
import com.common.lib.utils.enums.RolEnum;

public interface RolServiceInter {
     Roles findByNombreRol(RolEnum nombreRol);
}
