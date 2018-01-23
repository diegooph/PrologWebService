package br.com.zalf.prolog.webservice.commons.dashboard.components;

import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponentBuilder;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class QuantidadeItemComponent extends DashboardComponent {

    @NotNull
    private String qtdItens;
    @NotNull
    private String urlIcone;
    @NotNull
    private String backgroundColor;

    QuantidadeItemComponent(@NotNull String titulo,
                            @Nullable String subtitulo,
                            @NotNull String descricao,
                            @NotNull String qtdItens,
                            @NotNull String urlIcone,
                            @NotNull String backgroundColor) {
        super(titulo, subtitulo, descricao);
        this.qtdItens = qtdItens;
        this.urlIcone = urlIcone;
        this.backgroundColor = backgroundColor;
    }

    @NotNull
    public String getQtdItens() {
        return qtdItens;
    }

    public void setQtdItens(@NotNull String qtdItens) {
        this.qtdItens = qtdItens;
    }

    @NotNull
    public String getUrlIcone() {
        return urlIcone;
    }

    public void setUrlIcone(@NotNull String urlIcone) {
        this.urlIcone = urlIcone;
    }

    @NotNull
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(@NotNull String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "QuantidadeItemComponent{" +
                "qtdItens='" + qtdItens + '\'' +
                ", urlIcone='" + urlIcone + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }

    public static class Builder implements DashboardComponentBuilder {
        private String titulo;
        private String subtitulo;
        private String descricao;
        private String qtdItens;
        private String urlIcone;
        private String backgroundColor;

        public Builder() {}

        public Builder(@NotNull String titulo, @NotNull String descricao) {
            this.titulo = titulo;
            this.descricao = descricao;
        }

        @Override
        public Builder withTitulo(@NotNull String titulo) {
            this.titulo = titulo;
            return this;
        }

        @Override
        public Builder withSubtitulo(@Nullable String subtitulo) {
            this.subtitulo = subtitulo;
            return this;
        }

        @Override
        public Builder withDescricao(@NotNull String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder withQtdItens(@NotNull String qtdItens) {
            this.qtdItens = qtdItens;
            return this;
        }

        public Builder withUrlIcone(@NotNull String urlIcone) {
            this.urlIcone = urlIcone;
            return this;
        }

        public Builder withBackgroundColor(@NotNull String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @Override
        public DashboardComponent build() {
            Preconditions.checkNotNull(qtdItens, "qtdItens deve ser instanciada com 'withQtdItens'");
            Preconditions.checkNotNull(urlIcone, "urlIcone deve ser instanciada com 'withUrlIcone'");
            Preconditions.checkNotNull(backgroundColor, "backgroundColor deve ser instanciada com 'withBackgroundColor'");
            return new QuantidadeItemComponent(titulo, subtitulo, descricao, qtdItens, urlIcone, backgroundColor);
        }
    }
}
