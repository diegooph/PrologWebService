package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-01
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoListagem {
    @NotNull
    private final Long codMotivoMovimento;
    @NotNull
    private final String descricaoMotivoMovimento;
    private final boolean ativo;
}
