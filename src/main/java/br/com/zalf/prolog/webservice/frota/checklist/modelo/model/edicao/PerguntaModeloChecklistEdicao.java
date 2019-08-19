package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistEdicao extends PerguntaModeloChecklist {
    public static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_EDICAO";

    public PerguntaModeloChecklistEdicao() {
        super(TIPO_SERIALIZACAO);
    }
}
