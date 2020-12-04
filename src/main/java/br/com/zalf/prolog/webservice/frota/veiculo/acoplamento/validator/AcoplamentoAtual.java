package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-02
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
@Nullable
public class AcoplamentoAtual {

    private final Long codProcessoAcoplamento;

    private final Long codUnidadeAcoplamento;

    private final Long codVeiculo;
    private final short codPosicao;

    private final Long codDiagramaVeiculo;

    private final Boolean motorizado;
}
