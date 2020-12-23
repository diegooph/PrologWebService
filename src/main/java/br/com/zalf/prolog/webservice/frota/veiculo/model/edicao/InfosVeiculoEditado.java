package br.com.zalf.prolog.webservice.frota.veiculo.model.edicao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class InfosVeiculoEditado {
    @NotNull
    private final Long codVeiculo;
    @Nullable
    private final Long codEdicaoHistoricoAntigoEstadoVeiculo;
    @Nullable
    private final Long codEdicaoHistoricoNovoEstadoVeiculo;
    private final int totalEdicoes;
    @NotNull
    private final VeiculoAntesEdicao veiculoAntesEdicao;

    public boolean algoMudou() {
        return codEdicaoHistoricoAntigoEstadoVeiculo != null
                && codEdicaoHistoricoNovoEstadoVeiculo != null
                && totalEdicoes > 0;
    }
}