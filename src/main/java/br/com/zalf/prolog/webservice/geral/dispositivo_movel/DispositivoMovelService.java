package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelService {
    @NotNull
    private static final String TAG = DispositivoMovelService.class.getSimpleName();
    @NotNull
    private final DispositivoMovelDao dao = Injection.provideDispositivoMovelDao();

    @NotNull
    public List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws ProLogException {
        try {
            return dao.getMarcasDispositivos();
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao buscar as marcas de dispositivos móveis";
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar as marcas de dispositivos móveis, tente novamente");
        }
    }

    @NotNull
    public List<DispositivoMovel> getDispositivosPorEmpresa(@NotNull final Long codEmpresa) throws ProLogException {
        try {
            return dao.getDispositivosPorEmpresa(codEmpresa);
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao buscar as informações de dispositivos móveis";
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar as informações de dispositivos móveis, tente novamente");
        }
    }

    @NotNull
    public DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa, @NotNull final Long codDispositivo) throws ProLogException {
        try {
            return dao.getDispositivoMovel(codEmpresa, codDispositivo);
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao buscar o dispositivo móvel";
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar o dispositivo móvel, tente novamente");
        }
    }
}