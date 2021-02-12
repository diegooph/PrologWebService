package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.mapper;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoProjection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoMapper<T extends AfericaoDto, P extends AfericaoProjection> {

    @NotNull
    List<T> toDtos(@NotNull final List<P> projections);
    @NotNull
    T toDto(@NotNull final P projection);
}
