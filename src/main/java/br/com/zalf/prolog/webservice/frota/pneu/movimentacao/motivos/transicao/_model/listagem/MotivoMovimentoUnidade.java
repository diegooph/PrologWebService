package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-24
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoUnidade {
    @NotNull
    private final Long codMotivoMovimento;
    @NotNull
    private final String descricaoMotivoMovimento;
}
