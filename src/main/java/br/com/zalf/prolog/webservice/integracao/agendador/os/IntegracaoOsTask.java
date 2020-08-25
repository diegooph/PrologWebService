package br.com.zalf.prolog.webservice.integracao.agendador.os;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
    public static final String TAG = IntegracaoOsTask.class.getSimpleName();
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
            } catch (final Throwable t) {
                Log.e(TAG, "Erro ao buscar as informações das O.S's no banco de dados", t);
                throw Injection
                        .provideProLogExceptionHandler()
                        .map(t, "Erro ao tentar sincronizar as O.S's");
            }
        }
    }

    private void completarInformacoesChecklist() throws Throwable {
        for (final Long codOs : codOsSincronizar) {
            //noinspection ConstantConditions
            osSincronizar.add(Injection.provideIntegracaoDao().getOsIntegracaoByCod(codOs));
        }
    }

    private void enviarOrdensServico() {
        //noinspection ConstantConditions
        for (final OsIntegracao osIntegracao : osSincronizar) {
            final AvaCorpAvilanRequesterImpl requester = new AvaCorpAvilanRequesterImpl();
            try {
                requester.insertChecklistOs(infosEnvioOsIntegracao,
                        AvaCorpAvilanConverter.convert(osIntegracao));
            } catch (final Exception e) {
                Log.e(TAG, String.format("Erro ao enviar a OS: %s", osIntegracao.getCodOsProlog()), e);
            }
        }
    }

}
