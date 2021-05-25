package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class PneuListagemDto {
    @NotNull
    Long codPneu;
    @NotNull
    Long codRegionalPneu;
    @NotNull
    String nomeRegionalPneu;
    @NotNull
    Long codUnidadePneu;
    @NotNull
    String nomeUnidadePneu;
    @NotNull
    String codigoCliente;
    @NotNull
    Long codModeloPneu;
    @NotNull
    Long codDimensaoPneu;
    @NotNull
    BigDecimal valorPneu;
    @Nullable
    String dotPneu;
    @Nullable
    Integer posicaoAplicado;
    boolean pneuNovoNuncaRodado;
}
