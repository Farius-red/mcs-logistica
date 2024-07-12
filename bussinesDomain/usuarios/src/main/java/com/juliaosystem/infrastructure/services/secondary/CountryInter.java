package com.juliaosystem.infrastructure.services.secondary;

import com.common.lib.api.dtos.user.CountryDTO;
import com.common.lib.api.response.PlantillaResponse;

import java.util.List;
import java.util.Optional;

public interface CountryInter {
    Optional<PlantillaResponse<List<CountryDTO>>> findAll();
}
