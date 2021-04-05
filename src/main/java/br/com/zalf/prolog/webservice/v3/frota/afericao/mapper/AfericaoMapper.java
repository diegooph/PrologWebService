package br.com.zalf.prolog.webservice.v3.frota.afericao.mapper;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.DadosGeraisAfericao;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.projections.AfericaoProjection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoMapper<T, P extends AfericaoProjection> {

    @NotNull
    List<T> toDtos(@NotNull final List<P> projections);

    @NotNull
    T toDto(@NotNull final P projection);

    default DadosGeraisAfericao getDadosGerais(@NotNull final P projection) {
        return DadosGeraisAfericao.builder()
                .codigo(projection.getCodigo())
                .codUnidade(projection.getCodUnidade())
                .dataHora(projection.getDataHora())
                .tipoMedicaoColetada(projection.getTipoMedicaoColetadaAfericao())
                .tipoProcessoColeta(projection.getTipoProcessoColetaAfericao())
                .tempoRealizacao(projection.getTempoRealizacaoAfericaoInMillis())
                .formaColetaDados(projection.getFormaColetaDadosAfericao())
                .cpfAferidor(projection.getCpfAferidor())
                .nomeAferidor(projection.getNomeAferidor())
                .build();
    }
}
