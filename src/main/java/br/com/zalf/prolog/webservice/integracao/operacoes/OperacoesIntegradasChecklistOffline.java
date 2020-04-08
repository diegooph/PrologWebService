package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-08
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasChecklistOffline {
    @NotNull
    Long insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable;
}
