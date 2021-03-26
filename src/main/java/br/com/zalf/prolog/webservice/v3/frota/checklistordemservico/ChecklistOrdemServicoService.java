package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class ChecklistOrdemServicoService {

    @NotNull
    private final ChecklistOrdemServicoItemDao checklistOrdemServicoItemDao;

    @Autowired
    public ChecklistOrdemServicoService(@NotNull final ChecklistOrdemServicoItemDao checklistOrdemServicoItemDao) {
        this.checklistOrdemServicoItemDao = checklistOrdemServicoItemDao;
    }

    @NotNull
    public ChecklistOrdemServicoItemEntity getByCodigo(@NotNull final Long codigo) {
        return checklistOrdemServicoItemDao.getOne(codigo);
    }

    public void update(@NotNull final ChecklistOrdemServicoItemEntity checklistOrdemServicoItemEntity) {
        checklistOrdemServicoItemDao.save(checklistOrdemServicoItemEntity);
    }
}
