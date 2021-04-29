package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcopladoMantido {
    @NotNull
    private final Long codProcessoAcoplamento;
    @NotNull
    private final Long codUnidadeAcoplamento;
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final Long codDiagramaVeiculo;
    @NotNull
    private final Boolean motorizado;
    private final short posicaoAcaoRealizada;
    @Nullable
    private final Long kmColetado;
}
