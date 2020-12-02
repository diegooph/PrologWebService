package br.com.zalf.prolog.webservice.mappers;

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
    D toDto(E entity);
    E toEntity(D dto);

    default List<D> listEntitiesToDtos(final List<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    default List<E> listDtosToEntities(final List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
