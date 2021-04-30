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
    @NotNull
    private final Long codMovimentacao;
    @NotNull
    private final Long codPneu;
    @NotNull
    private final String codigoClientePneu;
    @NotNull
    private final Long codDimensaoPneu;
    @NotNull
    private final Integer vidaPneu;
    @Nullable
    private final Double sulcoInternoMomentoMovimentacaoEmMilimetros;
    @Nullable
    private final Double sulcoCentralInternoMomentoMovimentacaoEmMilimetros;
    @Nullable
    private final Double sulcoCentralExternoMomentoMovimentacaoEmMilimetros;
    @Nullable
    private final Double sulcoExternoMomentoMovimentacaoEmMilimetros;
    @Nullable
    private final Double pressaoMomentoMovimentacaoEmPsi;
    @NotNull
    private final String tipoOrigem;
    @Nullable
    private final Long posicaoPneuOrigem;
    @NotNull
    private final String tipoDestino;
    @Nullable
    private final Long posicaoPneuDestino;
    @Nullable
    private final String observacao;
    @Nullable
    private final Long codMotivoDescarte;
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
    @Nullable
    private final String codColeta;
}
