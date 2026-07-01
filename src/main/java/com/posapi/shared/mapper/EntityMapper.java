package com.posapi.shared.mapper;

/**
 * Interface base para mappers que proporciona métodos comunes.
 *
 * @param <E> la entidad
 * @param <D> el DTO
 */
public interface EntityMapper<E, D> {

    /**
     * Convierte una entidad a DTO.
     *
     * @param entity la entidad
     * @return el DTO
     */
    D toDTO(E entity);

    /**
     * Convierte un DTO a entidad.
     *
     * @param dto el DTO
     * @return la entidad
     */
    E toEntity(D dto);
}
