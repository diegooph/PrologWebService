package br.com.zalf.prolog.webservice.mappers;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-12-02
 *
 * @param <D> dto
 * @param <E> entity
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface Mapper<D, E> {

    /**
     * <p>
     * Método responsavel por mapear entidade para DTO.
     * </p>
     *
     * @param entity para mapeamento.
     * @return entidade mapeada para DTO especifico.
     *
     * @since 2020-12-02
     */
    @NotNull
    D toDto(@NotNull final E entity);

    /**
     * <p>
     * Método responsavel por mapear DTO para entidade.
     * </p>
     *
     * @param dto para mapeamento.
     * @return DTO mapeado para entidade.
     *
     * @since 2020-12-02
     */
    @NotNull
    E toEntity(@NotNull final D dto);

    /**
     * <p>
     * Método responsavel por mapear uma lista de entidades para DTOs.
     * utiliza-se do metodo toDto
     * para mapeamento dentro da stream de dados.
     * </p>
     *
     * @param entities para mapeamento.
     * @return lista de entidades mapeadas para DTO.
     *
     * @see #toDto(E entity)
     * @since 2020-12-02
     */
    @NotNull
    default List<D> listEntitiesToDtos(@NotNull final List<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * <p>
     * Método responsavel por mapear uma lista de DTOs para as entidades.
     * utiliza-se do metodo toEntity
     * para mapeamento dentro da stream de dados.
     * </p>
     *
     * @param dtos para mapeamento.
     * @return lista de DTOs mapeadas para entidade.
     *
     * @see #toEntity(D dto)
     * @since 2020-12-02
     */
    @NotNull
    default List<E> listDtosToEntities(@NotNull final List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
