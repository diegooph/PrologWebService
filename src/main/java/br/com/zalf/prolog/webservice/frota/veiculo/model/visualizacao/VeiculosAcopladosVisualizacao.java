package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-11-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public class VeiculosAcopladosVisualizacao {
    @NotNull
    private final Long codProcessoAcoplamento;
    @NotNull
    private final List<VeiculoAcopladoVisualizacao> veiculosAcoplados;
}
