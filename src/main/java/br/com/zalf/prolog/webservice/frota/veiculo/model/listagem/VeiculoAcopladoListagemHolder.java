package br.com.zalf.prolog.webservice.frota.veiculo.model.listagem;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 2020-11-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcopladoListagemHolder {
    public static final VeiculoAcopladoListagemHolder EMPTY = new VeiculoAcopladoListagemHolder(Collections.emptyMap());
    @NotNull
    private final Map<Long, List<VeiculoAcopladoListagem>> veiculosAcoplados;

    @NotNull
    public List<VeiculoAcopladoListagem> getVeiculosAcopladosByCodVeiculo(@NotNull final Long codVeiculo) {
        return veiculosAcoplados.getOrDefault(codVeiculo, Collections.emptyList());
    }
}
