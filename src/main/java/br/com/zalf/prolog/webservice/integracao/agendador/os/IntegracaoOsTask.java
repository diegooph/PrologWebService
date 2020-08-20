package br.com.zalf.prolog.webservice.integracao.agendador.os;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.InfosEnvioOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.OsIntegracao;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    @NotNull
    private final InfosEnvioOsIntegracao infosEnvioOsIntegracao;

    public IntegracaoOsTask(@NotNull final List<Long> codOsSincronizar,
                            @NotNull final InfosEnvioOsIntegracao infosEnvioOsIntegracao) {
        this.codOsSincronizar = codOsSincronizar;
        this.infosEnvioOsIntegracao = infosEnvioOsIntegracao;
    }

    @SneakyThrows
    @Override
    public void run() {
        if (!codOsSincronizar.isEmpty()) {
            osSincronizar = new ArrayList<>();
            completarInformacoesChecklist();
            enviarOrdensServico();
        }
    }

    private void completarInformacoesChecklist() throws Throwable {
        for (final Long codOs : codOsSincronizar) {
            final OsIntegracao os = Injection.provideIntegracaoDao().getOsIntegracaoByCod(codOs);
            //noinspection ConstantConditions
            osSincronizar.add(os);
        }
    }

    private void enviarOrdensServico() {
        for (final OsIntegracao osIntegracao : osSincronizar) {

        }
    }

}
