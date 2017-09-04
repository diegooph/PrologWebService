package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;
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

    /**
     * Caso o colaborador tenha a permissão {@link Pilares.Gente.Intervalo#MARCAR_INTERVALO} ou alguém
     * da sua unidade possuir, esse objeto conterá um {@link List<Colaborador>} que possuirá
     * todos os colaboradores que têm essa permissão.
     * O objeto também conterá um {@link List<TipoIntervalo>} referente aos tipos de intervalo da unidade,
     * além disso um {@link Date} sinalizando a última vez que este objeto sofreu atualização.
     */
    @NotNull
    private IntervaloOfflineSupport intervaloOfflineSupport;

    /**
     * Caso o colaborador tenha a permissão {@link Pilares.Gente.Intervalo#MARCAR_INTERVALO}, essa
     * lista conterá os {@link TipoIntervalo} que existem para sua unidade. Se ele não tiver essa
     * permissão, a lista será {@code null}.
     */
    @Nullable
    @Deprecated
    private List<TipoIntervalo> tiposIntervalos;

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

    public IntervaloOfflineSupport getIntervaloOfflineSupport() {
        return intervaloOfflineSupport;
    }

    public void setIntervaloOfflineSupport(IntervaloOfflineSupport intervaloOfflineSupport) {
        this.intervaloOfflineSupport = intervaloOfflineSupport;
    }

    public List<TipoIntervalo> getTiposIntervalos() {
        return tiposIntervalos;
    }

    public void setTiposIntervalos(List<TipoIntervalo> tiposIntervalos) {
        this.tiposIntervalos = tiposIntervalos;
    }
}