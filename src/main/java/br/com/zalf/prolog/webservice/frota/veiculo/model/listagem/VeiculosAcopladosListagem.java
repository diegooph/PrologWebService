package br.com.zalf.prolog.webservice.frota.veiculo.model.listagem;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-11-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public class VeiculosAcopladosListagem {
    @NotNull
    private final Long codProcessoAcoplamento;
    @NotNull
    private final List<VeiculoAcopladoListagem> veiculosAcoplados;

    public void add(@NotNull final VeiculoAcopladoListagem veiculoAcopladoListagem) {
        veiculosAcoplados.add(veiculoAcopladoListagem);
    }
}
