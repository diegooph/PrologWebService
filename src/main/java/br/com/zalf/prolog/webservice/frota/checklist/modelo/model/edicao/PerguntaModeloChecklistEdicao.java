package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistEdicao extends PerguntaModeloChecklist {
    public static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_EDICAO";

    /**
     * Quando um modelo de checklist é editado, indica qual foi a operação de edição realizada nessa pergunta.
     */
    private AcaoEdicaoPergunta acaoEdicao;

    public PerguntaModeloChecklistEdicao() {
        super(TIPO_SERIALIZACAO);
    }

    public AcaoEdicaoPergunta getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoPergunta acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
    }
}
