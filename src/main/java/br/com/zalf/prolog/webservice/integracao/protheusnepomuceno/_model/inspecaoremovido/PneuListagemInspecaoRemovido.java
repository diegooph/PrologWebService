package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.DEFAULT_CODIGOS_SEPARATOR;

@Data
public class PneuListagemInspecaoRemovido {
    @NotNull
    private final String codPneu;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final String codEmpresaPneu;
    @NotNull
    private final String codUnidadePneu;
    @NotNull
    private final Integer vidaAtualPneu;
    @NotNull
    private final Integer vidaTotalPneu;
    @NotNull
    private final Double pressaoRecomendadaPneu;
    @NotNull
    private final Double pressaoAtualPneu;
    @NotNull
    private final Double sulcoInternoPneu;
    @NotNull
    private final Double sulcoCentralInternoPneu;
    @NotNull
    private final Double sulcoCentralExternoPneu;
    @NotNull
    private final Double sulcoExternoPneu;
    @NotNull
    private final String dotPneu;
    @NotNull
    private final String nomeModeloPneu;
    @NotNull
    private final String codModeloPneu;
    @NotNull
    private final Integer qtdSulcosModeloPneu;
    @Nullable
    private final String nomeModeloBanda;
    @Nullable
    private final String codModeloBanda;
    @Nullable
    private final Integer qtdSulcosModeloBanda;

    public boolean isRecapado() {
        return this.vidaAtualPneu > 1;
    }

    @NotNull
    public String getCodEmpresaFilialVeiculo() {
        return this.codEmpresaPneu.concat(DEFAULT_CODIGOS_SEPARATOR).concat(this.getCodUnidadePneu());
    }
}