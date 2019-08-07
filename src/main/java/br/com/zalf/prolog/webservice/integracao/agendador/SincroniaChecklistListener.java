package br.com.zalf.prolog.webservice.integracao.agendador;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SincroniaChecklistListener {
    void onSincroniaOk(@NotNull final Checklist checklist);

    void onSincroniaNaoExecutada(@NotNull final Checklist checklist);

    void onErroSincronia(@NotNull final Checklist checklist, @Nullable final Throwable t);
}
