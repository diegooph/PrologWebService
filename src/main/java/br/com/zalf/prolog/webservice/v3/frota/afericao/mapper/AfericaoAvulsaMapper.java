package br.com.zalf.prolog.webservice.v3.frota.afericao.mapper;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.projections.AfericaoAvulsaProjection;
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
public class AfericaoAvulsaMapper implements AfericaoMapper<AfericaoAvulsaDto, AfericaoAvulsaProjection> {

    @Override
    @NotNull
    public List<AfericaoAvulsaDto> toDtos(@NotNull final List<AfericaoAvulsaProjection> projections) {
        return projections.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public AfericaoAvulsaDto toDto(@NotNull final AfericaoAvulsaProjection projection) {
        return AfericaoAvulsaDto.of(getDadosGerais(projection));
    }
}
