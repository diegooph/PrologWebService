package br.com.zalf.prolog.webservice.integracao.agendador.os;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.InfosEnvioOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConverter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
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
    @NotNull
    private final InfosEnvioOsIntegracao infosEnvioOsIntegracao;
    @Nullable
    private List<OsIntegracao> osSincronizar;

    public IntegracaoOsTask(@NotNull final List<Long> codOsSincronizar,
                            @NotNull final InfosEnvioOsIntegracao infosEnvioOsIntegracao) {
        this.codOsSincronizar = codOsSincronizar;
        this.infosEnvioOsIntegracao = infosEnvioOsIntegracao;
    }

    @Override
    public void run() {
        if (!codOsSincronizar.isEmpty()) {
            osSincronizar = new ArrayList<>();
            try {
                completarInformacoesChecklist();
                enviarOrdensServico();
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void completarInformacoesChecklist() throws Throwable {
        for (final Long codOs : codOsSincronizar) {
            //noinspection ConstantConditions
            osSincronizar.add(Injection.provideIntegracaoDao().getOsIntegracaoByCod(codOs));
        }
    }

    private void enviarOrdensServico() throws Exception {
        //noinspection ConstantConditions
        for (final OsIntegracao osIntegracao : osSincronizar) {
            final AvaCorpAvilanRequesterImpl requester = new AvaCorpAvilanRequesterImpl();
            requester.insertChecklistOs(infosEnvioOsIntegracao,
                    AvaCorpAvilanConverter.convert(osIntegracao));
        }
    }

}
