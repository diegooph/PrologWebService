package br.com.zalf.prolog.webservice.frota.kmprocessos.visitor;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AlterarKmProcessoVisitor {

    void visitChecklist(@NotNull final ChecklistKmProcesso checklistKmProcesso);
}
