package com.juliaosystem.infrastructure.services.primary;

import com.common.lib.api.dtos.user.CountryDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.adapters.primary.CountryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryImpl countryImpl;

    public Optional<PlantillaResponse<List<CountryDTO>>> all(){
        return countryImpl.findAll();
    }
}
