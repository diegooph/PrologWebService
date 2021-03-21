package br.com.zalf.prolog.webservice.frota.pneu.v3.mapper;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuMapper<E, D> {

    @NotNull
    D toDto(@NotNull final E entity);

    @NotNull
    E toEntity(@NotNull final D dto);

}
