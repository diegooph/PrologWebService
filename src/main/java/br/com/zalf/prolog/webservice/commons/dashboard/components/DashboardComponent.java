package br.com.zalf.prolog.webservice.commons.dashboard.components;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class DashboardComponent {

    @NotNull
    private String titulo;
    @Nullable
    private String subtitulo;
    @NotNull
    private String descricao;

    public DashboardComponent(@NotNull String titulo,
                              @Nullable String subtitulo,
                              @NotNull String descricao) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descricao = descricao;
    }

    @NotNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NotNull String titulo) {
        this.titulo = titulo;
    }

    @Nullable
    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(@Nullable String subtitulo) {
        this.subtitulo = subtitulo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }
}
