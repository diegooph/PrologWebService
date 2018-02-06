package br.com.zalf.prolog.webservice.dashboard.components;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
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
    private final String qtdItens;
    @NotNull
    private final String urlIcone;
    @NotNull
    private final Color backgroundColor;

    public static QuantidadeItemComponent createDefault(@NotNull final ComponentDataHolder component,
                                                        final int qtdItens) {
        return new QuantidadeItemComponent.Builder()
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withQtdItens(String.valueOf(qtdItens))
                .withBackgroundColor(Color.fromHex(component.corBackgroundHex))
                .withUrlIcone(component.urlIcone)
                .build();
    }

    private QuantidadeItemComponent(@NotNull String titulo,
                                    @Nullable String subtitulo,
                                    @NotNull String descricao,
                                    @NotNull String urlEndpointDados,
                                    @NotNull Integer codTipoComponente,
                                    int qtdBlocosHorizontais,
                                    int qtdBlocosVerticais,
                                    int ordemExibicao,
                                    @NotNull String qtdItens,
                                    @NotNull String urlIcone,
                                    @NotNull Color backgroundColor) {
        super(IdentificadorTipoComponente.QUANTIDADE_ITEM, titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
        this.qtdItens = qtdItens;
        this.urlIcone = urlIcone;
        this.backgroundColor = backgroundColor;
    }

    @NotNull
    public String getQtdItens() {
        return qtdItens;
    }

    @NotNull
    public String getUrlIcone() {
        return urlIcone;
    }

    @NotNull
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return "QuantidadeItemComponent{" +
                "qtdItens='" + qtdItens + '\'' +
                ", urlIcone='" + urlIcone + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }

    public static class Builder extends BaseComponentBuilder {
        private String qtdItens;
        private String urlIcone;
        private Color backgroundColor;

        public Builder() {}

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

        @Override
        public Builder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            super.withUrlEndpointDados(urlEndpointDados);
            return this;
        }

        @Override
        public Builder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            super.withCodTipoComponente(codTipoComponente);
            return this;
        }

        @Override
        public Builder withQtdBlocosHorizontais(int qtdBlocosHorizontais) {
            super.withQtdBlocosHorizontais(qtdBlocosHorizontais);
            return this;
        }

        @Override
        public Builder withQtdBlocosVerticais(int qtdBlocosVerticais) {
            super.withQtdBlocosVerticais(qtdBlocosVerticais);
            return this;
        }

        @Override
        public Builder withOrdemExibicao(int ordemExibicao) {
            super.withOrdemExibicao(ordemExibicao);
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

        public Builder withBackgroundColor(@NotNull Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @Override
        public QuantidadeItemComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(qtdItens, "qtdItens deve ser instanciada com 'withQtdItens'");
            Preconditions.checkNotNull(urlIcone, "urlIcone deve ser instanciada com 'withUrlIcone'");
            Preconditions.checkNotNull(backgroundColor, "backgroundColor deve ser instanciada com 'withBackgroundColor'");
            return new QuantidadeItemComponent(
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    qtdItens,
                    urlIcone,
                    backgroundColor);
        }
    }
}
