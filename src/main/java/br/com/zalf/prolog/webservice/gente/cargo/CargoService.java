package br.com.zalf.prolog.webservice.gente.cargo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.cargo._model.*;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoService {
    @NotNull
    private static final String TAG = CargoService.class.getSimpleName();
    @NotNull
    private final CargoDao dao = Injection.provideCargoDao();

    @NotNull
    public List<CargoSelecao> getTodosCargosUnidade(final Long codUnidade) throws ProLogException {
        try {
            return dao.getTodosCargosUnidade(codUnidade);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar todos os cargos da unidade %d", codUnidade);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar todos os cargos, tente novamente");
        }
    }

    @NotNull
    public List<CargoListagemEmpresa> getTodosCargosEmpresa(final Long codEmpresa) throws ProLogException {
        try {
            return dao.getTodosCargosEmpresa(codEmpresa);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar todos os cargos da empresa %d", codEmpresa);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar todos os cargos, tente novamente");
        }
    }


    @NotNull
    public CargoEdicao getByCod(final Long codEmpresa, final Long codigo) throws ProLogException {
        try {
            return dao.getByCod(codEmpresa, codigo);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar o cargo: %d", codigo);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar o cargo, tente novamente");
        }
    }

    @NotNull
    public List<CargoEmUso> getCargosEmUsoUnidade(final Long codUnidade) throws ProLogException {
        try {
            return dao.getCargosEmUsoUnidade(codUnidade);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar cargos em uso na unidade %d", codUnidade);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os cargos em uso, tente novamente");
        }
    }

    @NotNull
    public List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(final Long codUnidade) throws ProLogException {
        try {
            return dao.getCargosNaoUtilizadosUnidade(codUnidade);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar cargos não utilizados na unidade %d", codUnidade);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os cargos não utilizados, tente novamente");
        }
    }

    @NotNull
    public CargoVisualizacao getPermissoesDetalhadasUnidade(final Long codUnidade,
                                                            final Long codCargo) throws ProLogException {
        try {
            return dao.getPermissoesDetalhadasUnidade(codUnidade, codCargo);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar as permissões detalhadas da unidade %d", codUnidade), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar as permissões, tente novamente");
        }
    }

    @NotNull
    public AbstractResponse insertCargo(CargoInsercao cargo, final String userToken) throws ProLogException{
        try{
            return ResponseWithCod.ok("Cargo inserido com sucesso", dao.insertCargo(cargo, userToken));
        } catch (final Throwable e){
            Log.e(TAG, "Erro ao inserir cargo para a empresa: " + cargo.getCodEmpresa(), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao inserir o cargo");
        }
    }

    @NotNull
    public Response updateCargo(final CargoEdicao cargo, final String userToken) throws ProLogException {
        try {
            dao.updateCargo(cargo, userToken);
            return Response.ok("Cargo atualizado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar o cargo", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o cargo, tente novamente");
        }
    }

    @NotNull
    public Response deleteCargo(final Long codEmpresa, final Long codigo, final String userToken) throws ProLogException {
        try {
            dao.deleteCargo(codEmpresa, codigo, userToken);
            return Response.ok("Cargo deletado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao deletar o cargo", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao deletar o cargo, tente novamente");
        }
    }
}