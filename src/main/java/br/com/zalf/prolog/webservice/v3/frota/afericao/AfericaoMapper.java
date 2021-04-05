package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoPlacaProjection;
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
public class AfericaoMapper {
    @NotNull
    public List<AfericaoPlacaDto> toAfericaoPlacaDto(@NotNull final List<AfericaoPlacaProjection> afericoesPlacas) {
        return afericoesPlacas.stream()
                .map(this::toAfericaoPlacaDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public AfericaoPlacaDto toAfericaoPlacaDto(@NotNull final AfericaoPlacaProjection afericaoPlaca) {
        return AfericaoPlacaDto.of(afericaoPlaca.getKmVeiculo(),
                                   afericaoPlaca.getPlacaVeiculo(),
                                   afericaoPlaca.getIdentificadorFrota(),
                                   afericaoPlaca.getCodigo(),
                                   afericaoPlaca.getCodUnidade(),
                                   afericaoPlaca.getDataHora(),
                                   afericaoPlaca.getTipoMedicaoColetadaAfericao(),
                                   afericaoPlaca.getTipoProcessoColetaAfericao(),
                                   afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                                   afericaoPlaca.getFormaColetaDadosAfericao(),
                                   afericaoPlaca.getCpfAferidor(),
                                   afericaoPlaca.getNomeAferidor());
    }

    @NotNull
    public List<AfericaoAvulsaDto> toAfericaoAvulsaDto(@NotNull final List<AfericaoAvulsaProjection> afericoesAvulsas) {
        return afericoesAvulsas.stream()
                .map(this::toAfericaoAvulsaDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public AfericaoAvulsaDto toAfericaoAvulsaDto(@NotNull final AfericaoAvulsaProjection afericaoAvulsa) {
        return AfericaoAvulsaDto.of(afericaoAvulsa.getCodigo(),
                                    afericaoAvulsa.getCodUnidade(),
                                    afericaoAvulsa.getDataHora(),
                                    afericaoAvulsa.getTipoMedicaoColetadaAfericao(),
                                    afericaoAvulsa.getTipoProcessoColetaAfericao(),
                                    afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                                    afericaoAvulsa.getFormaColetaDadosAfericao(),
                                    afericaoAvulsa.getCpfAferidor(),
                                    afericaoAvulsa.getNomeAferidor());
    }
}
