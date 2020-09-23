package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class EdicaoVeiculo {

    @NotNull
    private final TipoAlteracaoEnum tipoAlteracao;
    @Nullable
    private final Object valorAntigo;
    @Nullable
    private final Object valorNovo;

}
