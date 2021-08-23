package br.com.zalf.prolog.webservice.gente.cargo;

import br.com.zalf.prolog.webservice.gente.cargo._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CargoDao {

    @NotNull
    List<CargoSelecao> getTodosCargosUnidade(@NotNull final Long codUnidade) throws Throwable;
    
    @NotNull
    List<CargoListagemEmpresa> getTodosCargosEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    CargoEdicao getByCod(@NotNull final Long codEmpresa,
                         @NotNull final Long codigo) throws Throwable;
    @NotNull
    List<CargoEmUso> getCargosEmUsoUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    CargoVisualizacao getPermissoesDetalhadasUnidade(@NotNull final Long codUnidade,
                                                     @NotNull final Long codCargo) throws Throwable;
    @NotNull
    Long insertCargo(@NotNull final CargoInsercao cargo,
                     @NotNull final String userToken) throws Throwable;

    void updateCargo(@NotNull final CargoEdicao cargo,
                     @NotNull final String userToken) throws Throwable;

    void deleteCargo(@NotNull final Long codEmpresa,
                     @NotNull final Long codigo,
                     @NotNull final String userToken) throws Throwable;
}