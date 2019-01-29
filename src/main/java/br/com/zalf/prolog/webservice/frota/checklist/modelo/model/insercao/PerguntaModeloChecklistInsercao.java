package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistInsercao extends PerguntaModeloChecklist {
    public static final String TIPO_SERIALIZACAO = "PERGUNTA_MODELO_CHECKLIST_INSERCAO";

    public PerguntaModeloChecklistInsercao() {
        super(TIPO_SERIALIZACAO);
    }
}