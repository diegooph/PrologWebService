package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class VeiculoAfericaoProtheusNepomuceno {
    /**
     * Atributo alfanumérico que representa o código único.
     */
    @NotNull
    private final String codVeiculo;

    /**
     * Atributo alfanumérico que representa a placa.
    */
    @NotNull
    private final String placaVeiculo;

    /**
     * Atributo alfanumérico que representa o código da empresa.
     */
    @NotNull
    private final String codEmpresaVeiculo;

    /**
     * Atributo alfanumérico que representa o código da unidade.
     */
    @NotNull
    private final String codUnidadeVeiculo;

    /**
     * Atributo alfanumérico que representa o código de frota.
     */
    @NotNull
    private final String codigoFrota;

    /**
     * Valor numérico que representa a quilimetragem atual.
     */
    @NotNull
    private final Long kmAtualVeiculo;

    /**
     * Atributo alfanumérico que representa o código de estrutura.
     */
    @NotNull
    private final String codEstruturaVeiculo;

    /**
     * Lista de {@link PneuAplicadoProtheusNepomuceno pneusAplicados}.
     */
    @NotNull
    private final List<PneuAplicadoProtheusNepomuceno> pneusAplicados;

    public VeiculoAfericaoProtheusNepomuceno(@NotNull final String codVeiculo,
                                             @NotNull final String placaVeiculo,
                                             @NotNull final String codEmpresaVeiculo,
                                             @NotNull final String codUnidadeVeiculo,
                                             @NotNull final String codigoFrota,
                                             @NotNull final Long kmAtualVeiculo,
                                             @NotNull final String codEstruturaVeiculo,
                                             @NotNull final List<PneuAplicadoProtheusNepomuceno> pneusAplicados) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.codEmpresaVeiculo = codEmpresaVeiculo;
        this.codUnidadeVeiculo = codUnidadeVeiculo;
        this.codigoFrota = codigoFrota;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.codEstruturaVeiculo = codEstruturaVeiculo;
        this.pneusAplicados = pneusAplicados;
    }

    @NotNull
    static VeiculoAfericaoProtheusNepomuceno getMedicaoDummy() {
        final List<PneuAplicadoProtheusNepomuceno> pneusAplicados = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pneusAplicados.add(PneuAplicadoProtheusNepomuceno.getPneuAplicadoDummy());
        }
        return new VeiculoAfericaoProtheusNepomuceno(
                "1234",
                "ZZZ0000",
                "E0001",
                "F0001",
                "Z01",
                12L,
                "F0001:M0001",
                pneusAplicados
        );
    }

    @NotNull
    public String getCodVeiculo() { return codVeiculo; }

    @NotNull
    public String getPlacaVeiculo() { return placaVeiculo; }

    @NotNull
    public String getCodEmpresaVeiculo() { return codEmpresaVeiculo; }

    @NotNull
    public String getCodUnidadeVeiculo() { return codUnidadeVeiculo; }

    @NotNull
    public String getCodigoFrota() { return codigoFrota; }

    @NotNull
    public Long getKmAtualVeiculo() { return kmAtualVeiculo; }

    @NotNull
    public String getCodEstruturaVeiculo() { return codEstruturaVeiculo; }

    @NotNull
    public List<PneuAplicadoProtheusNepomuceno> getPneusAplicados() {
        return pneusAplicados;
    }
}
