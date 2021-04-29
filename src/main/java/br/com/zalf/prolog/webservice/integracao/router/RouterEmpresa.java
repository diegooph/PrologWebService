package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

public class RouterEmpresa extends Router {
    private RouterEmpresa(@NotNull final IntegracaoDao integracaoDao,
                          @NotNull final IntegradorProLog integradorProLog,
                          @NotNull final String userToken,
                          @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    @NotNull
    public static RouterEmpresa create(@NotNull final EmpresaDao empresaDao,
                                       @NotNull final String userToken) {
        return new RouterEmpresa(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withEmpresaDao(empresaDao)
                        .build(),
                userToken,
                RecursoIntegrado.EMPRESA);
    }
}
