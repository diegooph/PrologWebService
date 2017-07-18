package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorDatabase;
import com.sun.istack.internal.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    private final Integrador integradorHttp;

    protected Sistema(@NotNull final Integrador integradorHttp) {
        this.integradorHttp = checkNotNull(integradorHttp, "integradorHttp não pode ser nulo!");
    }

    /**
     * Cada Sistema terá que implementar esse método, desse modo, cada um instancia o {@link IntegradorDatabase}
     * apenas com os DAOs que irá utilizar.
     *
     * @return um {@link Integrador} com o banco de dados do ProLog.
     */
    protected abstract Integrador getIntegradorDatabase();

    protected Integrador getIntegradorHttp() {
        return integradorHttp;
    }
}