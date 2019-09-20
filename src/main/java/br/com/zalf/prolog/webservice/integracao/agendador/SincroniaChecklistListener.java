package br.com.zalf.prolog.webservice.integracao.agendador;


import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SincroniaChecklistListener {
    void onSincroniaOk(@NotNull final Checklist checklist, @NotNull final Boolean isLastChecklist);

    void onSincroniaNaoExecutada(@NotNull final Checklist checklist, @NotNull final Boolean isLastChecklist);

    void onErroSincronia(@NotNull final Checklist checklist,
                         @NotNull final Boolean isLastChecklist,
                         @Nullable final Throwable t);
}
