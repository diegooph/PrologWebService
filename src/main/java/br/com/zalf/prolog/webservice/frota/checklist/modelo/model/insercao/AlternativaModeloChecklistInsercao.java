package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistInsercao extends AlternativaModeloChecklist {
    public static final String TIPO_SERIALIZACAO = "ALTERNATIVA_MODELO_CHECKLIST_INSERCAO";

    public AlternativaModeloChecklistInsercao() {
        super(TIPO_SERIALIZACAO);
    }
}