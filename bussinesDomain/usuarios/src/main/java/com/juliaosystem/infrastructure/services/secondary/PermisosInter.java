package com.juliaosystem.infrastructure.services.secondary;

import com.juliaosystem.infrastructure.entitis.Permiso;
import com.common.lib.utils.enums.PermisoEnum;

public interface PermisosInter  {
    Permiso findByNombrePermiso(PermisoEnum nombrePermiso);
}
