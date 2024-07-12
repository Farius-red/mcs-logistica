package com.juliaosystem.api.mappers;


import com.common.lib.api.mappers.PlantillaMapperGetDTO;
import com.common.lib.api.mappers.PlantillaMapperGetEntity;

/**
 * @description Recibe en el parametro T la entidad y en D Dto
 * @Autor daniel juliao
 * @param <T> entidad
 * @param <D> dto
 * @version 1
 */
public interface PlantillaMapers<T,D> extends PlantillaMapperGetEntity<T, D>, PlantillaMapperGetDTO<T,D> {

}
