package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.entities.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.entities.ChecklistV3Dao;
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
    private final ChecklistV3Dao checklistDao;

    @Autowired
    public AlterarKmProcessoVisitorImpl(@NotNull final ChecklistV3Dao checklistDao) {
        this.checklistDao = checklistDao;
    }

    @Override
    public void visitChecklist(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        final ChecklistEntity entity = checklistDao.getOne(checklistKmProcesso.getCodProcesso());
    }
}
