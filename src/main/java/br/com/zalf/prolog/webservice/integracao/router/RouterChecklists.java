package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import com.google.common.util.concurrent.Futures;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterChecklists extends Router {

    public static RouterChecklists create(@NotNull final ChecklistDao checklistDao, @NotNull final String userToken) {
        return new RouterChecklists(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withChecklistDao(checklistDao)
                        .withTipoVeiculoDao(Injection.provideTipoVeiculoDao())
                        .build(),
                userToken,
                RecursoIntegrado.CHECKLIST);
    }

    private RouterChecklists(@NotNull final IntegracaoDao integracaoDao,
                             @NotNull final IntegradorProLog integradorProLog,
                             @NotNull final String userToken,
                             @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}