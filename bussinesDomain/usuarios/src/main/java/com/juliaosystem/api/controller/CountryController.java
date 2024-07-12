package com.juliaosystem.api.controller;


import com.juliaosystem.infrastructure.services.primary.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.common.lib.api.dtos.user.CountryDTO;
import com.common.lib.api.response.PlantillaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/countries")
@Tag(name = "countries" , description = "Endpoints relacionados el uso de paises")
@RequiredArgsConstructor
public class CountryController {


  private final CountryService countryService;

    @Operation(summary = "optiene  lista de paises y ciudades", responses = {
            @ApiResponse(responseCode = "200", description = "lista de ciudades y paises con coidgo")
    })
    @GetMapping("/all")
    public ResponseEntity<PlantillaResponse<List<CountryDTO>>> all (){
        PlantillaResponse<List<CountryDTO>> response = countryService.all().orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
