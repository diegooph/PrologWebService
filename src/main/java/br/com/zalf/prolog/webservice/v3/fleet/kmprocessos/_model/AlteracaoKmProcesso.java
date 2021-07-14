package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class AlteracaoKmProcesso {
    @NotNull
    Long codEmpresa;
    @NotNull
    Long codVeiculo;
    @NotNull
    Long codProcesso;
    @NotNull
    VeiculoTipoProcesso tipoProcesso;
    @Nullable
    Long codColaboradorAlteracao;
    @NotNull
    Long novoKm;
}
