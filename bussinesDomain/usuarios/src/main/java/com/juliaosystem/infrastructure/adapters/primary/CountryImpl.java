package com.juliaosystem.infrastructure.adapters.primary;


import com.common.lib.api.dtos.user.CountryDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.adapters.secondary.CountryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryImpl {


    private  final CountryAdapter countryAdapter;

    public Optional<PlantillaResponse<List<CountryDTO>>> findAll() {
        return countryAdapter.findAll();
    }

}
