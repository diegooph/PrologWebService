package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradas;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 18/07/17.
 */
public interface RouterFlow {
    void proced(@NotNull final OperacoesIntegradas operacoesIntegradas);
    void cancel();
}
