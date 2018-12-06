package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistEdicao extends PerguntaModeloChecklist {
    private static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_EDICAO";

    private List<AlternativaModeloChecklist> alternativas;
    /**
     * Quando um modelo de checklist é editado, indica qual foi a operação de edição realizada nessa pergunta.
     */
    private AcaoEdicaoPergunta acaoEdicao;

    public PerguntaModeloChecklistEdicao() {
        super(TIPO_SERIALIZACAO);
    }

    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        return alternativas;
    }

    @Override
    public void setAlternativas(final List<AlternativaModeloChecklist> alternativas) {
        this.alternativas = alternativas;
    }

    public AcaoEdicaoPergunta getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoPergunta acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
    }
}
