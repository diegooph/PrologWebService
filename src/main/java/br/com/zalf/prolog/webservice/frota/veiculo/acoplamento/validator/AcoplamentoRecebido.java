package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-02
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class AcoplamentoRecebido {
    @Nullable
    private final Long codProcessoAcoplamento;
    @NotNull
    private final Long codUnidadeAcoplamento;
    @NotNull
    private final Long codVeiculo;
    private final short codPosicao;
    @NotNull
    private final Long codDiagramaVeiculo;
    @NotNull
    private final Boolean motorizado;
}

