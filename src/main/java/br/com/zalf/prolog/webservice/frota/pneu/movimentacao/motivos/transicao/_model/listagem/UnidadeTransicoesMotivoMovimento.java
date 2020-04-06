package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class UnidadeTransicoesMotivoMovimento {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final List<TransicaoUnidadeMotivos> origensDestinos;
}
