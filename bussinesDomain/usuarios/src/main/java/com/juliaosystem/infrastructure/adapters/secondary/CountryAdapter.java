package com.juliaosystem.infrastructure.adapters.secondary;

import com.common.lib.api.dtos.user.CountryDTO;
import com.juliaosystem.api.mappers.CountryMapper;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.entitis.Country;
import com.juliaosystem.infrastructure.repository.CountryRepository;
import com.juliaosystem.infrastructure.services.secondary.CountryInter;
import com.common.lib.utils.UserResponses;
import com.common.lib.utils.enums.ResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryAdapter implements CountryInter {


    private  final CountryRepository countryRepository;

    private  final CountryMapper countrymapper;
     private final UserResponses userResponses;

    @Override
    public Optional<PlantillaResponse<List<CountryDTO>>> findAll() {
       List<CountryDTO> countryDTOList = countrymapper.getListDTO((List<Country>) countryRepository.findAll());
        return (!countryDTOList.isEmpty())
                ? Optional.ofNullable(userResponses.buildResponse(ResponseType.GET.getCode(), CountryDTO.builder().build(), countryDTOList))
                : Optional.ofNullable(userResponses.buildResponse(ResponseType.NO_ENCONTRADO.getCode(), CountryDTO.builder().build()));
    }
}
