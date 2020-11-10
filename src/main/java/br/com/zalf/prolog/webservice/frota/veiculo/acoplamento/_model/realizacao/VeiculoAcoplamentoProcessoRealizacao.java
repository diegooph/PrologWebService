package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcoplamentoProcessoRealizacao {
    @NotNull
    private final Long codUnidade;
    @Nullable
    private final String observacao;
    @NotNull
    @Size(min = 2, max = 6)
    private final List<VeiculoAcoplamento> acoplamentos;
    @Nullable
    private final Long codProcessoAcoplamentoEditado;

    @NotNull
    public Optional<Long> estaEditandoProcessoAcoplamento() {
        return Optional.ofNullable(codProcessoAcoplamentoEditado);
    }
}
