package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConferenciaService  {
    private static final String TAG = VeiculoConferenciaService.class.getSimpleName();
    @NotNull
    private final VeiculoConferenciaDao dao = Injection.provideVeiculoConferenciaDao();

    @NotNull
    public List<Long> insert(@NotNull final InputStream fileInputStream) throws ProLogException {
        /*try {
            return dao.insert(VeiculoImportReader.readFromCsv(fileInputStream));
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao inserir pneus -- " + throwable.getMessage();
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(throwable, errorMessage);
        }*/
        return null;
    }

}
