package br.com.zalf.prolog.commons.login;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.questoes.Alternativa;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * Assim que o colaborador logar no app esse objeto será enviado, contendo o colaborador em si
 * e uma lista das alternativas do relato.
 */
public class LoginHolder {
    @NotNull
    private Colaborador colaborador;

    /**
     * Caso esse colaborador não tenha acesso a criar um novo relato, esse objeto será null.
     * Isso poderia ser obtido em outra busca, mas fazendo aqui, eliminamos o problema de o colaborador logar,
     * ir criar um novo relato, mas a lista de alternativas ainda não ter sido baixada.
     */
    @Nullable
    private List<Alternativa> alternativasRelato;

    /**
     * As credenciais de acesso a Amazon. Será diferente de null se o colaborador tiver acesso ao envio de relato
     * ou gsd.
     */
    @Nullable
    private AmazonCredentials amazonCredentials;


    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public List<Alternativa> getAlternativasRelato() {
        return alternativasRelato;
    }

    public void setAlternativasRelato(List<Alternativa> alternativasRelato) {
        this.alternativasRelato = alternativasRelato;
    }

    public AmazonCredentials getAmazonCredentials() {
        return amazonCredentials;
    }

    public void setAmazonCredentials(AmazonCredentials amazonCredentials) {
        this.amazonCredentials = amazonCredentials;
    }
}