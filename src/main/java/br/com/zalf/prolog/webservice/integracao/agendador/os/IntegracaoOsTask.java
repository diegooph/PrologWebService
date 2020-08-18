package br.com.zalf.prolog.webservice.integracao.agendador.os;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.OsIntegracao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-08-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class IntegracaoOsTask implements Runnable {

    @NotNull
    private final List<Long> codOsSincronizar;
    @Nullable
    private List<OsIntegracao> osSincronizar;

    public IntegracaoOsTask(@NotNull final List<Long> codOsSincronizar) {
        this.codOsSincronizar = codOsSincronizar;
    }

    @Override
    public void run() {
        if (!codOsSincronizar.isEmpty()) {
            completarInformacoesChecklist();
        }
    }

    private void completarInformacoesChecklist() {
        final ChecklistService service = new ChecklistService();
        codOsSincronizar.forEach(ccs -> {
            final OsIntegracao os = Injection.provideOrdemServicoDao().getOsByCod(ccs);
        });
    }

    /*
    private void buscaInformacoesBasicas() {
        urlEnvio = Injection
                .provideIntegracaoDao()
                .getUrl(codEmpresaProlog, getSistemaKey(), MetodoIntegrado.INSERT_OS);
    }
     */

}
