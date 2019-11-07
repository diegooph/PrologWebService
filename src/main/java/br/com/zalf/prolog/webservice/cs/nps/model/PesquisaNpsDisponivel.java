package br.com.zalf.prolog.webservice.cs.nps.model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsDisponivel {
    @NotNull
    private final Long codPesquisaNps;
    @NotNull
    private final String tituloPesquisa;
    @Nullable
    private final String breveDescricaoPesquisa;
    @NotNull
    private final String tituloPerguntaEscala;
    @NotNull
    private final String legendaEscalaBaixa;
    @NotNull
    private final String legendaEscalaAlta;
    @Nullable
    private final String tituloPerguntaDescritiva;
    private final boolean temPerguntaDescritiva;

    public PesquisaNpsDisponivel(@NotNull final Long codPesquisaNps,
                                 @NotNull final String tituloPesquisa,
                                 @Nullable final String breveDescricaoPesquisa,
                                 @NotNull final String tituloPerguntaEscala,
                                 @NotNull final String legendaEscalaBaixa,
                                 @NotNull final String legendaEscalaAlta,
                                 @Nullable final String tituloPerguntaDescritiva) {
        this.codPesquisaNps = codPesquisaNps;
        this.tituloPesquisa = tituloPesquisa;
        this.breveDescricaoPesquisa = breveDescricaoPesquisa;
        this.tituloPerguntaEscala = tituloPerguntaEscala;
        this.legendaEscalaBaixa = legendaEscalaBaixa;
        this.legendaEscalaAlta = legendaEscalaAlta;
        this.tituloPerguntaDescritiva = StringUtils.trimToNull(tituloPerguntaDescritiva);
        this.temPerguntaDescritiva = StringUtils.trimToNull(tituloPerguntaDescritiva) != null;
    }

    @NotNull
    public Long getCodPesquisaNps() {
        return codPesquisaNps;
    }

    @NotNull
    public String getTituloPesquisa() {
        return tituloPesquisa;
    }

    @Nullable
    public String getBreveDescricaoPesquisa() {
        return breveDescricaoPesquisa;
    }

    @NotNull
    public String getTituloPerguntaEscala() {
        return tituloPerguntaEscala;
    }

    @NotNull
    public String getLegendaEscalaBaixa() { return legendaEscalaBaixa; }

    @NotNull
    public String getLegendaEscalaAlta() { return legendaEscalaAlta; }

    @Nullable
    public String getTituloPerguntaDescritiva() {
        return tituloPerguntaDescritiva;
    }

    public boolean isTemPerguntaDescritiva() {
        return temPerguntaDescritiva;
    }
}