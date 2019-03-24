package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoSelecao;
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
}