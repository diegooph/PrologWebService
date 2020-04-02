package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model;

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
public final class MotivoMovimentoOrigemDestinoListagemMotivos {
    @NotNull
    private final OrigemDestinoEnum origemMovimento;
    @NotNull
    private final OrigemDestinoEnum destinoMovimento;
    @NotNull
    private final List<MotivoMovimentoOrigemDestinoListagemResumida> motivosRetirada;
    @Nullable
    private final Boolean obrigatorioMotivoRetirada;
}
