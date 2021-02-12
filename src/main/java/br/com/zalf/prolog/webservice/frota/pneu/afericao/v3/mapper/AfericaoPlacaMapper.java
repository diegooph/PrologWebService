package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.mapper;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoPlacaProjection;
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
    public List<AfericaoPlacaDto> toDtos(final @NotNull List<AfericaoPlacaProjection> projections) {
        return projections.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public AfericaoPlacaDto toDto(@NotNull final AfericaoPlacaProjection projection) {
        return AfericaoPlacaDto.builder()
                .codigo(projection.getCodigo())
                .codUnidade(projection.getCodUnidade())
                .dataHora(projection.getDataHora())
                .tipoMedicaoColetada(projection.getTipoMedicaoColetadaAfericao())
                .tempoRealizacao(projection.getTempoRealizacaoAfericaoInMillis())
                .formaColetaDados(projection.getFormaColetaDadosAfericao())
                .cpfAferidor(projection.getCpfAferidor())
                .nomeAferidor(projection.getNomeAferidor())
                .kmVeiculo(projection.getKmVeiculo())
                .placaVeiculo(projection.getPlacaVeiculo())
                .identificadorFrota(projection.getIdentificadorFrota())
                .build();
    }
}
