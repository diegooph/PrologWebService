package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class VeiculoAfericaoProtheusNepomuceno {
    /**
     * Atributo alfanumérico que representa o código único
     */
    @NotNull
    private final String codVeiculo;

    /**
     * Atributo alfanumérico que representa a placa
    */
    @NotNull
    private final String placaVeiculo;

    /**
     * Atributo alfanumérico que representa o código da empresa
     */
    @NotNull
    private final String codEmpresaVeiculo;

    /**
     * Atributo alfanumérico que representa o código da unidade
     */
    @NotNull
    private final String codUnidadeVeiculo;

    /**
     * Atributo alfanumérico que representa o código de frota
     */
    @NotNull
    private final String codigoFrota;

    /**
     * Valor numérico que representa a quilimetragem atual
     */
    @NotNull
    private final Long kmAtualVeiculo;

    /**
     * Atributo alfanumérico que representa o código de estrutura
     */
    @NotNull
    private final String codEstruturaVeiculo;

    public VeiculoAfericaoProtheusNepomuceno(@NotNull final String codVeiculo,
                                             @NotNull final String placaVeiculo,
                                             @NotNull final String codEmpresaVeiculo,
                                             @NotNull final String codUnidadeVeiculo,
                                             @NotNull final String codigoFrota,
                                             @NotNull final Long kmAtualVeiculo,
                                             @NotNull final String codEstruturaVeiculo) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.codEmpresaVeiculo = codEmpresaVeiculo;
        this.codUnidadeVeiculo = codUnidadeVeiculo;
        this.codigoFrota = codigoFrota;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.codEstruturaVeiculo = codEstruturaVeiculo;
    }

    @NotNull
    static VeiculoAfericaoProtheusNepomuceno getMedicaoDummy() {
        return new VeiculoAfericaoProtheusNepomuceno(
                "1234",
                "ZZZ0000",
                "E0001",
                "F0001",
                "Z01",
                12L,
                "F0001:M0001"
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
}
