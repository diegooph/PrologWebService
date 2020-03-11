package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class VeiculoListagemProtheusNepomuceno {
    /**
     * Atributo alfanumérico que representa a placa
    */
    @NotNull
    private final String placaVeiculo;

    /**
     * Atributo alfanumérico que representa o código único
     */
    @NotNull
    private final String codVeiculo;

    /**
     * Atributo alfanumérico que representa o código de frota
     */
    @NotNull
    private final String codigoFrota;

    /**
     * Atributo alfanumérico que representa o código da empresa
     */
    @NotNull
    private final String codEmpresaVeiculo;

    /**
     * Atributo alfanumérico que representa o código da filial
     */
    @NotNull
    private final String codFilialVeiculo;

    /**
     * Atributo alfanumérico que representa o modelo
     */
    @NotNull
    private final String nomeModeloVeiculo;

    /**
     * Atributo alfanumérico que representa o código do modelo
     */
    @NotNull
    private final String codModeloVeiculo;

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

    /**
     * Valor numérico que representa a quantidade de pneus aplicados
     */
    @NotNull
    private final Integer qtsPneusAplicadosVeiculo;

    public VeiculoListagemProtheusNepomuceno(@NotNull final String placaVeiculo,
                                             @NotNull final String codVeiculo,
                                             @NotNull final String codigoFrota,
                                             @NotNull final String codEmpresaVeiculo,
                                             @NotNull final String codFilialVeiculo,
                                             @NotNull final String nomeModeloVeiculo,
                                             @NotNull final String codModeloVeiculo,
                                             @NotNull final Long kmAtualVeiculo,
                                             @NotNull final String codEstruturaVeiculo,
                                             @NotNull final Integer qtsPneusAplicadosVeiculo) {
        this.placaVeiculo = placaVeiculo;
        this.codVeiculo = codVeiculo;
        this.codigoFrota = codigoFrota;
        this.codEmpresaVeiculo = codEmpresaVeiculo;
        this.codFilialVeiculo = codFilialVeiculo;
        this.nomeModeloVeiculo = nomeModeloVeiculo;
        this.codModeloVeiculo = codModeloVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.codEstruturaVeiculo = codEstruturaVeiculo;
        this.qtsPneusAplicadosVeiculo = qtsPneusAplicadosVeiculo;
    }

    @NotNull
    static VeiculoListagemProtheusNepomuceno getMedicaoDummy() {
        return new VeiculoListagemProtheusNepomuceno(
                "ZZZ0000",
                "1234",
                "Z01",
                "E0001",
                "F0001",
                "89",
                "123",
                12L,
                "F0001:M0001",
                8
        );
    }

    @NotNull
    public String getPlacaVeiculo() { return placaVeiculo; }

    @NotNull
    public String getCodVeiculo() { return codVeiculo; }

    @NotNull
    public String getCodigoFrota() { return codigoFrota; }

    @NotNull
    public String getCodEmpresaVeiculo() { return codEmpresaVeiculo; }

    @NotNull
    public String getCodFilialVeiculo() { return codFilialVeiculo; }

    @NotNull
    public String getNomeModeloVeiculo() { return nomeModeloVeiculo; }

    @NotNull
    public String getCodModeloVeiculo() { return codModeloVeiculo; }

    @NotNull
    public Long getKmAtualVeiculo() { return kmAtualVeiculo; }

    @NotNull
    public String getCodEstruturaVeiculo() { return codEstruturaVeiculo; }

    @NotNull
    public Integer getQtsPneusAplicadosVeiculo() { return qtsPneusAplicadosVeiculo; }
}
