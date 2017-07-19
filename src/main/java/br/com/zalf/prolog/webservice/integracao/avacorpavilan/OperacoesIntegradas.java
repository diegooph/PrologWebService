package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/18/17.
 */
public interface OperacoesIntegradas {
    @NotNull
    Integrador getIntegradorDatabase();
}