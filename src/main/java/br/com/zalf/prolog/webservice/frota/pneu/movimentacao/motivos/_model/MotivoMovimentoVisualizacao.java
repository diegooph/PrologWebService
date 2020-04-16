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
    private final Long codMotivoMovimento;
    @NotNull
    private final String descricaoMotivoMovimento;
    @Nullable
    private final String codAuxiliar;
    @NotNull
    private final LocalDateTime dataHoraUltimaAlteracao;
    @NotNull
    private final String nomeColaboradorUltimaAlteracao;
    private final boolean ativo;
}
