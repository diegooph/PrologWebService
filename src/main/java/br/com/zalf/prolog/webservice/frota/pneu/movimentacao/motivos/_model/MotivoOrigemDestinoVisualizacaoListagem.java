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
    private Long codMotivoOrigemDestino;

    @NotNull
    private String nomeEmpresa;

    @NotNull
    private String descricaoMotivo;

    @NotNull
    private OrigemDestinoEnum origem;

    @NotNull
    private OrigemDestinoEnum destino;

    @NotNull
    private boolean obrigatorio;

    @NotNull
    private LocalDateTime dataHoraUltimaAlteracao;

    @NotNull
    private String nomeColaboradorUltimaAlteracao;

}
