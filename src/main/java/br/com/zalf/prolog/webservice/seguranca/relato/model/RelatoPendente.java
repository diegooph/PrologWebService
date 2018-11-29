package br.com.zalf.prolog.webservice.seguranca.relato.model;

/**
 * Created on 29/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class RelatoPendente {

    private final int qtdRelatosPendentesClassificacao;
    private final int getQtdRelatosPendentesFechamento;

    public RelatoPendente(int qtdRelatosPendentesClassificacao,
                          int getQtdRelatosPendentesFechamento) {

        this.qtdRelatosPendentesClassificacao = qtdRelatosPendentesClassificacao;
        this.getQtdRelatosPendentesFechamento = getQtdRelatosPendentesFechamento;
    }

    public int getQtdRelatosPendentesClassificacao() {
        return qtdRelatosPendentesClassificacao;
    }

    public int getQtdRelatosPendentesFechamento() {
        return getQtdRelatosPendentesFechamento;
    }
}
