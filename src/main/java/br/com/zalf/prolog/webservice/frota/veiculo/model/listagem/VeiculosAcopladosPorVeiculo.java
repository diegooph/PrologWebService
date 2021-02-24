package br.com.zalf.prolog.webservice.frota.veiculo.model.listagem;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Created on 2020-11-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculosAcopladosPorVeiculo {
    public static final VeiculosAcopladosPorVeiculo EMPTY = new VeiculosAcopladosPorVeiculo(Collections.emptyMap());
    @NotNull
    private final Map<Long, VeiculosAcopladosListagem> veiculosAcoplados;

    @Nullable
    public VeiculosAcopladosListagem getVeiculosAcopladosByCodVeiculo(@NotNull final Long codVeiculo) {
        return veiculosAcoplados.get(codVeiculo);
    }
}
