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
public class MotivoRetiradaHistoricoListagem {

    @NotNull
    private final Long codMotivoRetirada;

    @NotNull
    private final String descricaoMotivoRetirada;

    @NotNull
    private final boolean ativoMotivoRetirada;

    @NotNull
    private final String codAuxiliarMotivoRetirada;

    @NotNull
    private final LocalDateTime dataHoraAlteracaoMotivoRetirada;

    @NotNull
    private final String nomeColaboradorAlteracaoMotivoRetirada;

}
