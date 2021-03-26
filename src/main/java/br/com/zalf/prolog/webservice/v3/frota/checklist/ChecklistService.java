package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class ChecklistService {

    @NotNull
    private final ChecklistDao checklistDao;

    @Autowired
    public ChecklistService(@NotNull final ChecklistDao checklistDao) {
        this.checklistDao = checklistDao;
    }

    @NotNull
    public ChecklistEntity getByCodigo(@NotNull final Long codigo) {
        return checklistDao.getOne(codigo);
    }
}
