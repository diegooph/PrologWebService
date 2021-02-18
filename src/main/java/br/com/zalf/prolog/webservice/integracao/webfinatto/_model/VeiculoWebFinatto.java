package br.com.zalf.prolog.webservice.integracao.webfinatto._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public class VeiculoWebFinatto {
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final String codVeiculo;
    @Nullable
    private final String codigoFrota;
    @NotNull
    private final String codEmpresaVeiculo;
    @NotNull
    private final String codFilialVeiculo;
    @NotNull
    private final String nomeModeloVeiculo;
    @NotNull
    private final String codModeloVeiculo;
    @NotNull
    private final String nomeMarcaVeiculo;
    @NotNull
    private final String codMarcaVeiculo;
    @NotNull
    private final Long kmAtualVeiculo;
    @NotNull
    private final String codEstruturaVeiculo;
    @Nullable
    private final Integer qtdPneusAplicadosVeiculo;
    @Nullable
    private final List<PneuWebFinatto> pneusAplicados;

    @NotNull
    public Integer getQtdPneusAplicadosVeiculo() {
        if (qtdPneusAplicadosVeiculo == null && pneusAplicados == null) {
            throw new IllegalStateException(
                    "As propriedades qtdPneusAplicadosVeiculo e pneusAplicados não podem ser nulas");
        }
        if (qtdPneusAplicadosVeiculo != null) {
            return qtdPneusAplicadosVeiculo;
        }
        return pneusAplicados.size();
    }
}
