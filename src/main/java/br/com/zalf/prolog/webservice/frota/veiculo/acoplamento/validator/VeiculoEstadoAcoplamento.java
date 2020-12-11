package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

@Data
public final class VeiculoEstadoAcoplamento {
    @NotNull
    private final Long codVeiculo;
    @Nullable
    private final Long codProcessoAcoplamentoVinculado;
    private final short posicaoAcoplado;

    public boolean isAcoplado() {
        return codProcessoAcoplamentoVinculado != null;
    }
}
