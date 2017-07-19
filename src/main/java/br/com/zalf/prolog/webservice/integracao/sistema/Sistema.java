package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradasVeiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import com.sun.istack.internal.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradasVeiculo {
    @NotNull
    private final Integrador integradorHttp;
    @NotNull
    private final Integrador integradorDatabase;

    protected Sistema(@NotNull final Integrador integradorHttp, Integrador integradoDatabase) {
        this.integradorHttp = checkNotNull(integradorHttp, "integradorHttp não pode ser nulo!");
        this.integradorDatabase = checkNotNull(integradoDatabase, "integradorDatabase não pode ser nulo!");
    }

    protected Integrador getIntegradorHttp() {
        return integradorHttp;
    }

    @Override
    public Integrador getIntegradorDatabase() {
        return integradorDatabase;
    }
}