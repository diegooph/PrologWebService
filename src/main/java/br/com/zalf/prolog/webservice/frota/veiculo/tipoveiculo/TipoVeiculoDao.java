package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TipoVeiculoDao {

    @NotNull
    Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;

    void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    TipoVeiculo getTipoVeiculo(@NotNull final Long codTipoVeiculo) throws Throwable;

    void deleteTipoVeiculoByEmpresa(@NotNull final Long codEmpresa,
                                    @NotNull final Long codTipoVeiculo) throws Throwable;
}
