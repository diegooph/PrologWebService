package br.com.zalf.prolog.webservice.interno.apresentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaService;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ApresentacaoService {
    @NotNull
    private static final String TAG = ApresentacaoService.class.getSimpleName();
    @NotNull
    private final ApresentacaoDao dao = Injection.provideApresentacaoDao();

    @NotNull
    public Response getResetaClonaEmpresaApresentacao(@NotNull final String authorization,
                                                      @NotNull final Long codEmpresaBase,
                                                      @NotNull final Long codEmpresaUsuario) throws ProLogException {
        // Deve ficar fora do try/catch porque não queremos mascarar erros de autentação com erros do processo de
        // import.
        final PrologInternalUser internalUser = new AutenticacaoInternaService().authorize(authorization);

        try {
            return Response.ok(dao.resetaEmpresaApresentacaoUsuario(
                    internalUser.getCodigo(),
                    codEmpresaBase,
                    codEmpresaUsuario));
        } catch (final Throwable throwable) {
            final String message = "Erro ao resetar e clonar empresa de apresentação";
            Log.e(TAG, message, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, message);
        }
    }
}
