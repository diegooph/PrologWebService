package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistVisualizacao extends PerguntaModeloChecklist {
    private static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_VISUALIZACAO";

    private List<AlternativaModeloChecklist> alternativas;

    public PerguntaModeloChecklistVisualizacao() {
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
}
