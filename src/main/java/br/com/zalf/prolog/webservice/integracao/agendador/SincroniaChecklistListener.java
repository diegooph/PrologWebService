package br.com.zalf.prolog.webservice.integracao.agendador;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SincroniaChecklistListener {
    void onSincroniaOk(@NotNull final Long codChecklist, @NotNull final Boolean isLastChecklist);

    void onSincroniaNaoExecutada(@NotNull final Long codChecklist, @NotNull final Boolean isLastChecklist);

    void onErroSincronia(@NotNull final Long codChecklist,
                         @NotNull final Boolean isLastChecklist,
                         @Nullable final Throwable t);
}
