package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class AlternativaModeloChecklistEdicao extends AlternativaModeloChecklist {
    private static final String TIPO_SERIALIZACAO = "ALTERNATIVA_MODELO_CHECKLIST_EDICAO";
    /**
     * Quando um modelo de checklist é editado, indica qual foi a operação de edição realizada nessa alternativa.
     */
    private AcaoEdicaoAlternativa acaoEdicao;

    public AlternativaModeloChecklistEdicao() {
        super(TIPO_SERIALIZACAO);
    }

    public AcaoEdicaoAlternativa getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoAlternativa acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
    }
}
