package br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class VeiculoAberturaServicoFiltro {
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @NotNull
    Long codServico;
}
