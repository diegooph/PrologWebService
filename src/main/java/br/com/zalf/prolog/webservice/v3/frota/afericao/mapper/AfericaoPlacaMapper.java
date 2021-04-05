package br.com.zalf.prolog.webservice.v3.frota.afericao.mapper;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.projections.AfericaoPlacaProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class AfericaoPlacaMapper implements AfericaoMapper<AfericaoPlacaDto, AfericaoPlacaProjection> {

    @Override
    @NotNull
    public List<AfericaoPlacaDto> toDtos(@NotNull final List<AfericaoPlacaProjection> projections) {
        return projections.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public AfericaoPlacaDto toDto(@NotNull final AfericaoPlacaProjection projection) {
        return AfericaoPlacaDto.of(projection.getKmVeiculo(),
                                   projection.getPlacaVeiculo(),
                                   projection.getIdentificadorFrota(),
                                   getDadosGerais(projection));
    }
}
