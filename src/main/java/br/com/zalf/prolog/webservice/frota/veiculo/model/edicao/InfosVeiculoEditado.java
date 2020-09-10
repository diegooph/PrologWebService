package br.com.zalf.prolog.webservice.frota.veiculo.model.edicao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-09-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class InfosVeiculoEditado {
    @NotNull
    private final Long codVeiculo;
    private final boolean algoMudou;
    @NotNull
    private final VeiculoAntesEdicao veiculoAntesEdicao;
}
