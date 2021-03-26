package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class AlteracaoKmProcessoDto {
    @NotNull
    Long codEmpresa;
    @NotNull
    Long codProcesso;
    @NotNull
    VeiculoTipoProcesso tipoProcesso;
    long novoKm;
}
