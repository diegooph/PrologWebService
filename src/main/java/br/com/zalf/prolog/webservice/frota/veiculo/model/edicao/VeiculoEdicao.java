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
public final class VeiculoEdicao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codEmpresaAlocado;
    @NotNull
    private final Long codUnidadeAlocado;
    @NotNull
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final Long codTipoVeiculo;
    @NotNull
    private final Long codModeloVeiculo;
    @NotNull
    private final Boolean possuiHubodometro;
    private final long kmAtualVeiculo;
    private final boolean statusAtivo;
}
