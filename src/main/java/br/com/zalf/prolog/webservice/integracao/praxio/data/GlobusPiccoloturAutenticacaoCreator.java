package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.AutenticacaoWebService;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.ObjectFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class GlobusPiccoloturAutenticacaoCreator {

    @NotNull
    static AutenticacaoWebService createCredentials() {
        final AutenticacaoWebService autenticacaoWebService = new ObjectFactory().createAutenticacaoWebService();
        autenticacaoWebService.setToken(GlobusPiccoloturConstants.TOKEN_AUTENTICACAO_OS);
        autenticacaoWebService.setShortCode(GlobusPiccoloturConstants.SHORT_CODE_AUTENTICACAO_OS);
        autenticacaoWebService.setNomeMetodo(GlobusPiccoloturConstants.METODO_PARA_LIBERAR);
        return autenticacaoWebService;
    }
}
