package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

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
public final class MotivoRetiradaOrigemDestinoVisualizacao {
    @NotNull
    private final Long codMotivoRetiradaOrigemDestino;
    @NotNull
    private final String nomeEmpresaMotivoRetirada;
    @NotNull
    private final String descricaoMotivoRetirada;
    @NotNull
    private final OrigemDestinoEnum origemMovimento;
    @NotNull
    private final OrigemDestinoEnum destinoMovimento;
    private final boolean obrigatorioMotivoRetirada;
    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracao;
    @NotNull
    private final String nomeColaboradorUltimaAlteracao;
}
