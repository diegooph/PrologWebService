package br.com.zalf.prolog.webservice.contato;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EntreEmContatoService {
    private static final String TAG = EntreEmContatoService.class.getSimpleName();
    @NotNull
    private final EntreEmContatoDao dao = new EntreEmContatoDaoImpl();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    public AbstractResponse insertNovoContato(@NotNull final MensagemContato contato) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Sua mensagem foi recebida, obrigado",
                    dao.insertNovaMensagemContato(contato));
        } catch (Throwable e) {
            final String errorMessage = "Erro ao salvar mensagem";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}
