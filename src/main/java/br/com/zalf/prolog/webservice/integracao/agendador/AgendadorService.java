package br.com.zalf.prolog.webservice.integracao.agendador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.integracao.praxio.ChecklistItensNokGlobusTask;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.GlobusPiccoloturRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.SistemaGlobusPiccoloturDaoImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;

/**
 * Created on 31/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AgendadorService implements SincroniaChecklistListener {
    @NotNull
    private static final String TAG = AgendadorService.class.getSimpleName();

    public void sincronizaChecklists() throws ProLogException {
        try {
            // Buscamos qual checklist precisa ser sincronizado. Por questões de dependência entre os checklists
            // realizados, não podemos sincronizar uma lista de checklists, deve ser um a um para garantir que a
            // contagem de apontamentos será incrementada corretamente.
            final Long codChecklistParaSincronizar = new IntegracaoPraxioService().getCodChecklistParaSincronizar();
            // Se codChecklistParaSincronizar == 0 então não tem nenhum checklist para ser sincronizado.
            if (codChecklistParaSincronizar == 0) {
                return;
            }
            final Checklist checklist = Injection.provideChecklistDao().getByCod(codChecklistParaSincronizar);
            // Executamos a sincronia utilizando a thread específica para esse serviço.
            Executors.newSingleThreadExecutor().execute(
                    new ChecklistItensNokGlobusTask(
                            codChecklistParaSincronizar,
                            checklist,
                            new SistemaGlobusPiccoloturDaoImpl(),
                            new GlobusPiccoloturRequesterImpl(),
                            this));
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao tentar sincronizar o checklist");
        }
    }

    @Override
    public void onSincroniaOk(@NotNull final Checklist checklist) {
        // Se a sincronia ocorreu com sucesso o sistema integrado irá devolver ao ProLog uma O.S Aberta. Neste momento
        // iremos disparar a sincronia de um novo checklist, não devemos fazer neste callback.
        Log.d(TAG, "Checklist sincronizado com sucesso: " + checklist.getCodigo());
    }

    @Override
    public void onSincroniaNaoExecutada(@NotNull final Checklist checklist) {
        Log.d(TAG, "Não foi preciso sincronizar o checklist: " + checklist.getCodigo());
        // Se a sincronia não foi executada, significa que não foi enviado um checklist para o sistema integrado, logo
        // não haverá uma resposta de O.S Aberta e o sistema não irá disparar a sincronia do próximo checklist. Por
        // esse motivo, forçamos a sincronia do próximo checklist.
        sincronizaChecklists();
    }

    @Override
    public void onErroSincronia(@NotNull final Checklist checklist, @Nullable final Throwable t) {
        Log.d(TAG, "Não foi possível sincronizar o checklist: " + checklist.getCodigo());
        // Se ocorreu erro na sincronia, significa que não foi enviado um checklist para o sistema integrado, logo
        // não haverá uma resposta de O.S Aberta e o sistema não irá disparar a sincronia do próximo checklist. Por
        // esse motivo, forçamos a sincronia do próximo checklist.
        sincronizaChecklists();
    }
}
