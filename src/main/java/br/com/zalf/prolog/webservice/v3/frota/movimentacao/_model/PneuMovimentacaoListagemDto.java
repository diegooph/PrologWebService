package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class PneuMovimentacaoListagemDto {
    private final long codPneu;
    @NotNull
    private final String codCliente;
    private final long codModelo;
    private final long codDimensao;
    private final int vidaAtual;
    @Nullable
    private final Double pressaoAtualEmPsi;
    @Nullable
    private final Double alturaSulcoInternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoCentralInternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoCentralExternoEmMilimetros;
    @Nullable
    private final Double alturaSulcoExternoEmMilimetros;
}
