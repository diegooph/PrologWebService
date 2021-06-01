package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class PneuListagemDto {
    @NotNull
    Long codPneu;
    @NotNull
    String codigoCliente;
    @NotNull
    Long codGrupoPneu;
    @NotNull
    String nomeGrupoPneu;
    @NotNull
    Long codUnidadePneu;
    @NotNull
    String nomeUnidadePneu;
    @NotNull
    Integer vidaAtualPneu;
    @NotNull
    Integer vidaTotalPneu;

    @NotNull
    Double pressaoRecomendadaPneuEmPsi;
    @Nullable
    Double pressaoAtualPneuEmPsi;
    @Nullable
    Double sulcoExternoPneuEmMilimetros;
    @Nullable
    Double sulcoCentralExternoPneuEmMilimetros;
    @Nullable
    Double sulcoCentralInternoPneuEmMilimetros;
    @Nullable
    Double sulcoInternoPneuEmMilimetros;
    @Nullable
    String dotPneu;

    @NotNull
    Long codDimensaoPneu;
    @NotNull
    Double alturaPneu;
    @NotNull
    Double larguraPneu;
    @NotNull
    Double aroPneu;

    @NotNull
    Long codMarcaPneu;
    @NotNull
    String nomeMarcaPneu;
    @NotNull
    Long codModeloPneu;
    @NotNull
    String nomeModeloPneu;
    @NotNull
    Integer qtdSulcosModeloPneu;
    @NotNull
    Double alturaSulcosModeloPneuEmMilimetros;
    @NotNull
    BigDecimal valorPneu;

    @Nullable
    Long codMarcaBanda;
    @Nullable
    String nomeMarcaBanda;
    @Nullable
    Long codModeloBanda;
    @Nullable
    String nomeModeloBanda;
    @Nullable
    Integer qtdSulcosModeloBanda;
    @Nullable
    Double alturaSulcosModeloBandaEmMilimetros;
    @Nullable
    BigDecimal valorBandaPneu;

    boolean pneuNovoNuncaRodado;

    @NotNull
    StatusPneu statusPneu;
    @Nullable
    Long codVeiculoPneuAplicado;
    @Nullable
    String placaVeiculoPneuAplicado;
    @Nullable
    String identificadorFrotaVeiculoPneuAplicado;
    @Nullable
    Integer posicaoAplicado;

    @Nullable
    Long codRecapadora;
    @Nullable
    String nomeRecapadora;
    @Nullable
    String codigoColeta;

    @Nullable
    Long codMotivoDescarte;
    @Nullable
    String urlFotoDescarte1;
    @Nullable
    String urlFotoDescarte2;
    @Nullable
    String urlFotoDescarte3;
}
