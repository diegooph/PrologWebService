package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final Long codEmpresa;

    @NotNull
    private final String descricaoMotivo;

    @NotNull
    private final boolean ativoMotivo;

    @NotNull
    private final LocalDateTime dataHoraInsercaoMotivo;

    @Nullable
    private final LocalDateTime dataHoraUltimaAlteracaoMotivo;

}
