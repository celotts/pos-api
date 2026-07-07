package com.posapi.shared.mapper;

import java.util.List;

/**
 * Interfaz base genérica para mappers de la aplicación.
 * Proporciona un contrato unificado para la conversión entre entidades de dominio y DTOs.
 *
 * @param <E> el tipo de la Entidad de Dominio o Persistencia
 * @param <D> el tipo del Data Transfer Object (DTO)
 */
public interface EntityMapper<E, D> {

    /**
     * Convierte una entidad a su correspondiente DTO.
     *
     * @param entity la entidad de origen
     * @return el DTO mapeado
     */
    D toDTO(E entity);

    /**
     * Convierte un DTO a su correspondiente entidad.
     *
     * @param dto el DTO de origen
     * @return la entidad mapeada
     */
    E toEntity(D dto);

    /**
     * Convierte una lista de entidades a una lista de DTOs.
     * MapStruct implementará esto de forma automática.
     *
     * @param entityList lista de entidades de origen
     * @return lista de DTOs mapeados
     */
    List<D> toDTO(List<E> entityList);

    /**
     * Convierte una lista de DTOs a una lista de entidades.
     * MapStruct implementará esto de forma automática.
     *
     * @param dtoList lista de DTOs de origen
     * @return lista de entidades mapeadas
     */
    List<E> toEntity(List<D> dtoList);
}
