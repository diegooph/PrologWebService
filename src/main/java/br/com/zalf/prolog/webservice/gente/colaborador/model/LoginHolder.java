package br.com.zalf.prolog.webservice.gente.colaborador.model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * O objeto também conterá um {@link List<TipoMarcacao>} referente aos tipos de intervalo da unidade,
     * além disso um {@link Date} sinalizando a última vez que este objeto sofreu atualização.
     */
    @NotNull
    private IntervaloOfflineSupport intervaloOfflineSupport;

    /**
     * Caso o colaborador tenha a permissão {@link Pilares.Gente.Intervalo#MARCAR_INTERVALO}, essa
     * lista conterá os {@link TipoMarcacao} que existem para sua unidade. Se ele não tiver essa
     * permissão, a lista será {@code null}.
     */
    @Nullable
    @Deprecated
    private List<TipoMarcacao> tiposIntervalos;

    /**
     * Valor booleano que indica se a empresa a qual o colaborador está cadastrado está liberada para realizar o
     * checklist de forma offline. Se <code>TRUE</code> então a aplicação permitirá que o checklist seja finalizado
     * mesmo sem conexão com a internet, caso <code>FALSE</code> então o processo de realização de checklist exigirá
     * que exista conexão para a realização.
     */
    private boolean checklistOfflineAtivoEmpresa;

    /**
     * Valor booleano que indica se a empresa a qual o colaborador está cadastrado está liberada para realizar o
     * checklist de diferentes unidades. Se <code>TRUE</code> então a aplicação permitirá que o colaborador selecione
     * de qual unidade ele realizará o checklist, tanto online quanto offline. Caso <code>FALSE</code>, então o
     * processo de realização de checklist só exibirá os modelos de checklist disponíveis na unidade onde o colaborador
     * que está realizando está cadastrado.
     */
    private boolean checklistDiferentesUnidadesAtivoEmpresa;

    public LoginHolder() {

    }

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

    @Deprecated
    public List<TipoMarcacao> getTiposIntervalos() {
        return tiposIntervalos;
    }

    @Deprecated
    public void setTiposIntervalos(List<TipoMarcacao> tiposIntervalos) {
        this.tiposIntervalos = tiposIntervalos;
    }

    public boolean isChecklistOfflineAtivoEmpresa() {
        return checklistOfflineAtivoEmpresa;
    }

    public void setChecklistOfflineAtivoEmpresa(final boolean checklistOfflineAtivoEmpresa) {
        this.checklistOfflineAtivoEmpresa = checklistOfflineAtivoEmpresa;
    }

    public boolean isChecklistDiferentesUnidadesAtivoEmpresa() {
        return checklistDiferentesUnidadesAtivoEmpresa;
    }

    public void setChecklistDiferentesUnidadesAtivoEmpresa(final boolean checklistDiferentesUnidadesAtivoEmpresa) {
        this.checklistDiferentesUnidadesAtivoEmpresa = checklistDiferentesUnidadesAtivoEmpresa;
    }
}