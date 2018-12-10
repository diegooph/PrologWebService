package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistVisualizacao extends PerguntaModeloChecklist {
    private static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_VISUALIZACAO";

    public PerguntaModeloChecklistVisualizacao() {
        super(TIPO_SERIALIZACAO);
    }

}