package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoHistoricoListagem {
    @NotNull
    private final Long codMotivoMovimento;
    @NotNull
    private final String descricaoMotivoMovimento;
    private final boolean ativoMotivoMovimento;
    @NotNull
    private final String codAuxiliarMotivoMovimento;
    @NotNull
    private final LocalDateTime dataHoraAlteracaoMotivoMovimento;
    @NotNull
    private final String nomeColaboradorAlteracaoMotivoMovimento;
}
