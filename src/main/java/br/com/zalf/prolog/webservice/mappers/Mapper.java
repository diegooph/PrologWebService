package br.com.zalf.prolog.webservice.mappers;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-12-02
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 * @param <D> dto
 * @param <E> entity
 */
public interface Mapper<D, E> {
    
    @NotNull
    D toDto(@NotNull final E entity);

    @NotNull
    E toEntity(@NotNull final D dto);

    @NotNull
    default List<D> listEntitiesToDtos(@NotNull final List<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    default List<E> listDtosToEntities(@NotNull final List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
