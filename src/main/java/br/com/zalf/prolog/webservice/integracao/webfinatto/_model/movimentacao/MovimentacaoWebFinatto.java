package br.com.zalf.prolog.webservice.integracao.webfinatto._model.movimentacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class MovimentacaoWebFinatto {
    @NotNull
    private final PneuMovimentavaoWebFinatto pneuMovimentacao;
    @NotNull
    private final String tipoOrigem;
    @Nullable
    private final String posicaoOrigemPneu;
    @NotNull
    private final String tipoDestino;
    @Nullable
    private final String posicaoDestinoPneu;
    @Nullable
    private final String observacaoProcessoMovimentacao;
}
