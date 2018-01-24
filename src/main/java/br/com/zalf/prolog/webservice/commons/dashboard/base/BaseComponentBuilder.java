package br.com.zalf.prolog.webservice.commons.dashboard.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class BaseComponentBuilder implements DashboardComponentBuilder {
    @NotNull
    protected String titulo;
    @Nullable
    protected String subtitulo;
    @NotNull
    protected String descricao;

    @Override
    public DashboardComponentBuilder withTitulo(@NotNull String titulo) {
        this.titulo = titulo;
        return this;
    }

    @Override
    public DashboardComponentBuilder withSubtitulo(@Nullable String subtitulo) {
        this.subtitulo = subtitulo;
        return this;
    }

    @Override
    public DashboardComponentBuilder withDescricao(@NotNull String descricao) {
        this.descricao = descricao;
        return this;
    }

    protected void ensureNotNullValues() {
        Preconditions.checkNotNull(titulo, "titulo deve ser instanciada com 'withTitulo'");
        Preconditions.checkNotNull(descricao, "titulo deve ser instanciada com 'withDescricao'");
    }
}