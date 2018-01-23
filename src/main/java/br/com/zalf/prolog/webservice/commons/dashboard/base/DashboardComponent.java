package br.com.zalf.prolog.webservice.commons.dashboard.base;

import com.google.common.base.Preconditions;
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
        Preconditions.checkNotNull(titulo, "titulo não pode ser null!");
        Preconditions.checkNotNull(subtitulo, "subtitulo não pode ser null!");
        Preconditions.checkNotNull(descricao, "descricao não pode ser null!");
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
