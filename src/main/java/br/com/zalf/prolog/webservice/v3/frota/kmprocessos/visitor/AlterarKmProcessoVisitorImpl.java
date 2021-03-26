package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public class AlterarKmProcessoVisitorImpl implements AlterarKmProcessoVisitor {

    @NotNull
    private final ChecklistDao checklistDao;

    @Autowired
    public AlterarKmProcessoVisitorImpl(@NotNull final ChecklistDao checklistDao) {
        this.checklistDao = checklistDao;
    }

    @Override
    public void visitChecklist(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        final ChecklistEntity entity = checklistDao.getOne(checklistKmProcesso.getCodProcesso());
    }
}
