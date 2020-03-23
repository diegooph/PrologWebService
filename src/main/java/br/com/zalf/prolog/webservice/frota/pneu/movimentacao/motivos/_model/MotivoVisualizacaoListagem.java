package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data class MotivoVisualizacaoListagem {

    @NotNull
    private final Long codMotivo;

    @NotNull
    private final String nomeEmpresa;

    @NotNull
    private final String descricaoMotivo;

    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracaoMotivo;

    @NotNull
    private final String nomeColaboradorUltimaAlteracao;

}
