package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class TransicaoUnidadeMotivos {
    @NotNull
    private final OrigemDestinoEnum origemMovimento;
    @NotNull
    private final OrigemDestinoEnum destinoMovimento;
    @NotNull
    private final List<MotivoMovimentoUnidade> motivosMovimento;
    @Nullable
    private final Boolean obrigatorioMotivoMovimento;
}
