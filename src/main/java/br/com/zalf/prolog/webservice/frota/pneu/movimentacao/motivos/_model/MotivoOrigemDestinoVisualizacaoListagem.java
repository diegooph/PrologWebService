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
public @Data class MotivoOrigemDestinoVisualizacaoListagem {

    @NotNull
    private final Long codMotivoOrigemDestino;

    @NotNull
    private final String nomeEmpresa;

    @NotNull
    private final String descricaoMotivo;

    @NotNull
    private final OrigemDestinoEnum origem;

    @NotNull
    private final OrigemDestinoEnum destino;

    @NotNull
    private final boolean obrigatorio;

    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracao;

    @NotNull
    private final String nomeColaboradorUltimaAlteracao;

}
