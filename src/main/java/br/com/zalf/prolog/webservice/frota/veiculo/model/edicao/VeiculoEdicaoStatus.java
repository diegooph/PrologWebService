package br.com.zalf.prolog.webservice.frota.veiculo.model.edicao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-09-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@AllArgsConstructor
public final class VeiculoEdicaoStatus {
    @NotNull
    private final Long codigo;
    private final boolean statusAtivo;
    private final boolean acoplado;
}
