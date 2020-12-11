package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

@Data
public class VeiculoEstadoAcoplamento {
    @NotNull
    private final Long codVeiculo;
    @Nullable
    private final Long codProcessoAcoplamento;
    @Nullable
    private final Short posicaoAcoplado;

    public boolean isAcoplado() {
        return codProcessoAcoplamento != null;
    }
}
