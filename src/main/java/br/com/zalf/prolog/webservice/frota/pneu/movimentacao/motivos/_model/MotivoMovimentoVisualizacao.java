package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-24
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoVisualizacao {
    @NotNull
    private final Long codMotivoRetirada;
    @NotNull
    private final String descricaoMotivoRetirada;
    private final boolean ativoMotivoRetirada;
    @Nullable
    private final String codAuxiliar;
    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracaoMotivo;
    @NotNull
    private final String nomeColaboradorUltimaAlteracao;
}
