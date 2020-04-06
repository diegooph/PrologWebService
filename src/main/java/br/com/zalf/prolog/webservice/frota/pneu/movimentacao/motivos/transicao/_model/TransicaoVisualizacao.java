package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class TransicaoVisualizacao {
    @NotNull
    private final Long codMotivoMovimentoTransicao;
    @NotNull
    private final String nomeEmpresaMotivoMovimento;
    @NotNull
    private final String descricaoMotivoMovimento;
    @NotNull
    private final OrigemDestinoEnum origemMovimento;
    @NotNull
    private final OrigemDestinoEnum destinoMovimento;
    private final boolean obrigatorioMotivoMovimento;
    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracao;
    @NotNull
    private final String nomeColaboradorUltimaAlteracao;
}
