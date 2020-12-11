package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;

@Data
public class AcoplamentoAtual {

    private final Long codProcessoAcoplamento;

    private final Long codUnidadeAcoplamento;

    private final Long codVeiculo;
    private final short codPosicao;

    private final Long codDiagramaVeiculo;

    private final Boolean motorizado;
}
