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
    @Nullable
    private final Long codVeiculoDestino;
    @Nullable
    private final String placaVeiculoDestino;
    @Nullable
    private final String idFrotaVeiculoDestino;
    @Nullable
    private final Long codDiagramaDestino;
    @Nullable
    private final Long kmColetadoVeiculoDestino;
    @NotNull
    private final String tipoDestino;
    @Nullable
    private final Long posicaoPneuDestino;
    @Nullable
    private final Long codMotivoDescarte;
    @Nullable
    private final String codColeta;
    @Nullable
    private final String urlImagemDescarte1;
    @Nullable
    private final String urlImagemDescarte2;
    @Nullable
    private final String urlImagemDescarte3;
    @Nullable
    private final Long codRecapadora;
    @Nullable
    private final String nomeRecapadora;
    @NotNull
    private final PneuMovimentacaoListagemDto pneuMovimentacao;
}
