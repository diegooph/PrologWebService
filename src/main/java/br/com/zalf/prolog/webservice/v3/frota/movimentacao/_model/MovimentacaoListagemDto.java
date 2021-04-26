package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MovimentacaoListagemDto {
    private final long codMovimentacao;
    private final long codUnidade;
    @Nullable
    private final Long codVeiculoOrigem;
    @Nullable
    private final String placaVeiculoOrigem;
    @Nullable
    private final String idFrotaVeiculoOrigem;
    @Nullable
    private final Long codDiagramaOrigem;
    @Nullable
    private final Long kmColetadoVeiculoOrigem;
    @NotNull
    private final String tipoOrigem;
}
