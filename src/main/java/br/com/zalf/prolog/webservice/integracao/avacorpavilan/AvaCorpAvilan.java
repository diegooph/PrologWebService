package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {

    public AvaCorpAvilan(@NotNull final Integrador integradorHttp, @NotNull final Integrador integradoDatabase) {
        super(integradorHttp, integradoDatabase);
    }
}