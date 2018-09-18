package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import org.jetbrains.annotations.NotNull;

/**
 * Agrupa uma marcação de início com sua equivalente de fim para exibição.
 *
 * Poderá conter apenas início ou apenas fim.
 *
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAgrupadaAjusteExibicao {
    /**
     * Uma marcação de início para exibição, pode ser <code>null</code>.
     */
    private MarcacaoAjusteExibicao marcacaoInicio;

    /**
     * Uma marcação de fim para exibição, pode ser <code>null</code>.
     */
    private MarcacaoAjusteExibicao marcacaoFim;

    public MarcacaoAgrupadaAjusteExibicao() {

    }

    @NotNull
    public static MarcacaoAgrupadaAjusteExibicao createDummy() {
        final MarcacaoAgrupadaAjusteExibicao intervaloAjuste = new MarcacaoAgrupadaAjusteExibicao();
        intervaloAjuste.setMarcacaoInicio(MarcacaoAjusteExibicao.createDummyInicio());
        intervaloAjuste.setMarcacaoFim(MarcacaoAjusteExibicao.createDummyFim());
        return intervaloAjuste;
    }

    public MarcacaoAjusteExibicao getMarcacaoInicio() {
        return marcacaoInicio;
    }

    public void setMarcacaoInicio(final MarcacaoAjusteExibicao marcacaoInicio) {
        this.marcacaoInicio = marcacaoInicio;
    }

    public MarcacaoAjusteExibicao getMarcacaoFim() {
        return marcacaoFim;
    }

    public void setMarcacaoFim(final MarcacaoAjusteExibicao marcacaoFim) {
        this.marcacaoFim = marcacaoFim;
    }

    @Override
    public String toString() {
        return "MarcacaoAgrupadaAjusteExibicao{" +
                "marcacaoInicio=" + marcacaoInicio +
                ", marcacaoFim=" + marcacaoFim +
                '}';
    }
}