package br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico._model.MovimentacaoPneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico._model.MovimentacaoPneuServicoRealizadoRecapadoraEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-06-24
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class MovimentacaoServicoRealizadoCreator {

    @NotNull
    public static MovimentacaoPneuServicoRealizadoEntity createMovimentacaoPneuServicoRealizado(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado,
            @NotNull final String fonteServicoRealizado) {
        return MovimentacaoPneuServicoRealizadoEntity.builder()
                .codMovimentacao(codMovimentacao)
                .codServicoRealizado(codPneuServicoRealizado)
                .fonteServicoRealizado(fonteServicoRealizado)
                .build();
    }

    @NotNull
    public static MovimentacaoPneuServicoRealizadoRecapadoraEntity createMovimentacaoPneuServicoRealizadoRecapadora(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado,
            @NotNull final Long codRecapadora) {
        return MovimentacaoPneuServicoRealizadoRecapadoraEntity.builder()
                .codMovimentacao(codMovimentacao)
                .codServicoRealizadoMovimentacao(codPneuServicoRealizado)
                .codRecapadora(codRecapadora)
                .build();
    }
}
