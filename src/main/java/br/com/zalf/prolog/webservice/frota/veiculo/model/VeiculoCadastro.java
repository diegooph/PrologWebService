package br.com.zalf.prolog.webservice.frota.veiculo.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2019-10-04
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoCadastro {
    @NotNull
    private final Long codEmpresaAlocado;
    @NotNull
    private final Long codUnidadeAlocado;
    @NotNull
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final Long codMarcaVeiculo;
    @NotNull
    private final Long codModeloVeiculo;
    @NotNull
    private final Long codTipoVeiculo;
    private final long kmAtualVeiculo;
    @NotNull
    private final Boolean possuiHubodometro;
}
