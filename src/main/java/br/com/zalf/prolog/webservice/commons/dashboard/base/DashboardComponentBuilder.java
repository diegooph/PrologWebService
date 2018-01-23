package br.com.zalf.prolog.webservice.commons.dashboard.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 23/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface DashboardComponentBuilder {

    DashboardComponentBuilder withTitulo(@NotNull String titulo);

    DashboardComponentBuilder withSubtitulo(@Nullable String subtitulo);

    DashboardComponentBuilder withDescricao(@NotNull String descricao);

    DashboardComponent build();
}
