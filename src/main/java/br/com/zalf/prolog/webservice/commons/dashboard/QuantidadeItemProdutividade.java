package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class QuantidadeItemProdutividade extends DashboardComponent {

    @NotNull
    private String qtdItens;
    @NotNull
    private String urlIcone;
    @NotNull
    private String backgroundColor;

    public QuantidadeItemProdutividade(@NotNull String titulo,
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
        return "QuantidadeItemProdutividade{" +
                "qtdItens='" + qtdItens + '\'' +
                ", urlIcone='" + urlIcone + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }
}
