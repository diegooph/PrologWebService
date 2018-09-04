package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteAtivacaoInativacao extends MarcacaoAjuste {

    private boolean isAtivo;

    public MarcacaoAjusteAtivacaoInativacao() {
        super(MarcacaoAjuste.MARCACAO_AJUSTE_ATIVACAO_INATIVACAO);
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(final boolean ativo) {
        isAtivo = ativo;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAtivacaoInativacao{" +
                "isAtivo=" + isAtivo +
                '}';
    }
}
