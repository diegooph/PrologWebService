package br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro;

import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-02-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class ServicosAbertosBuscaFiltro {
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    TipoServico tipoServico;
}
