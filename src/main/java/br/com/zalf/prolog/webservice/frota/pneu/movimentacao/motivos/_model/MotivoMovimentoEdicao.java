package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoEdicao {
    @NotNull
    private final Long codMotivoRetirada;
    @NotNull
    private final String descricaoMotivoRetirada;
    private final boolean ativoMotivoRetirada;
    @Nullable
    private final String codAuxiliarMotivoRetirada;
}
