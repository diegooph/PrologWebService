package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TipoVeiculoDiagrama {
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long codTipoVeiculo;
    @NotNull
    private final String nomeTipoVeiculo;
    private final boolean temDiagramaAssociado;

    public TipoVeiculoDiagrama(@NotNull final Long codVeiculo,
                               @NotNull final String placaVeiculo,
                               @NotNull final Long codTipoVeiculo,
                               @NotNull final String nomeTipoVeiculo,
                               final boolean temDiagramaAssociado) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.codTipoVeiculo = codTipoVeiculo;
        this.nomeTipoVeiculo = nomeTipoVeiculo;
        this.temDiagramaAssociado = temDiagramaAssociado;
    }

    @NotNull
    public Long getCodVeiculo() {
        return codVeiculo;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }

    @NotNull
    public String getNomeTipoVeiculo() {
        return nomeTipoVeiculo;
    }

    public boolean isTemDiagramaAssociado() {
        return temDiagramaAssociado;
    }
}
