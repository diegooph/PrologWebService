package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class SulcoPressao {
    @NotNull
    private final Long codPneu;
    @NotNull
    private final String codPneuCliente;
    private final double valorSulco;
    private final double valorPressao;

    public SulcoPressao(@NotNull final Long codPneu,
                        @NotNull final String codPneuCliente,
                        final double valorSulco,
                        final double valorPressao) {
        this.codPneu = codPneu;
        this.codPneuCliente = codPneuCliente;
        this.valorSulco = valorSulco;
        this.valorPressao = valorPressao;
    }

    @NotNull
    public Long getCodPneu() {
        return codPneu;
    }

    @NotNull
    public String getCodPneuCliente() {
        return codPneuCliente;
    }

    public double getValorSulco() {
        return valorSulco;
    }

    public double getValorPressao() {
        return valorPressao;
    }
}