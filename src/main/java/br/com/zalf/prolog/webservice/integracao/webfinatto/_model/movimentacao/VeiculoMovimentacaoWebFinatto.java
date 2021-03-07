package br.com.zalf.prolog.webservice.integracao.webfinatto._model.movimentacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class VeiculoMovimentacaoWebFinatto {
    @NotNull
    private final String codEmpresaVeiculo;
    @NotNull
    private final String codFilialVeiculo;
    @NotNull
    private final Long kmMomentoMovimentacao;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final String codVeiculo;
}
