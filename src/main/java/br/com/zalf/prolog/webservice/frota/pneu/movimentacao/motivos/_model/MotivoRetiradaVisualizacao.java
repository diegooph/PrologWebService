package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-24
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data class MotivoRetiradaVisualizacao {

    @NotNull
    private final Long codMotivoRetirada;

    @NotNull
    private final String descricaoMotivoRetirada;

    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracaoMotivo;

    @NotNull
    private final String nomeColaboradorUltimaAlteracao;

}
