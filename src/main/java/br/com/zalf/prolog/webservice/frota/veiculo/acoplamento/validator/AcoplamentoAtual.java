package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-02
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class AcoplamentoAtual {

    @Nullable
    private final Long codProcessoAcoplamento;
    @Nullable
    private final Long codUnidadeAcoplamento;
    @Nullable
    private final Long codVeiculo;
    private final short codPosicao;
    @Nullable
    private final Long codDiagramaVeiculo;
    @Nullable
    private final Boolean motorizado;
}
