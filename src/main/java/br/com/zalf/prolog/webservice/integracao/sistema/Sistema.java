package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorDatabase;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorHttp;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    private Integrador integradorDatabase;
    private Integrador integradorHttp;

    Integrador getIntegradorDatabase() {
        if (integradorDatabase == null) {
            integradorDatabase = new IntegradorDatabase.Builder()
                    .withChecklistDao(Injection.provideChecklistDao())
                    .withVeiculoDao(Injection.provideVeiculoDao())
                    .build();
        }

        return integradorDatabase;
    }

    Integrador getIntegradorHttp() {
        return new IntegradorHttp();
    }
}