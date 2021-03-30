package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class ChecklistOrdemServicoService {

    @NotNull
    private final ChecklistOrdemServicoDao checklistOrdemServicoDao;
    @NotNull
    private final ChecklistOrdemServicoItemDao checklistOrdemServicoItemDao;

    @Autowired
    public ChecklistOrdemServicoService(@NotNull final ChecklistOrdemServicoDao checklistOrdemServicoDao,
                                        @NotNull final ChecklistOrdemServicoItemDao checklistOrdemServicoItemDao) {
        this.checklistOrdemServicoDao = checklistOrdemServicoDao;
        this.checklistOrdemServicoItemDao = checklistOrdemServicoItemDao;
    }

    @NotNull
    public ChecklistOrdemServicoItemEntity getItemOrdemServicoByCodigo(@NotNull final Long codItemOrdemServico) {
        return checklistOrdemServicoItemDao.getOne(codItemOrdemServico);
    }

    public void updateItemOrdemServico(@NotNull final ChecklistOrdemServicoItemEntity checklistOrdemServicoItemEntity) {
        checklistOrdemServicoItemDao.save(checklistOrdemServicoItemEntity);
    }

    @Transactional
    public void updateKmFechamentoItem(@NotNull final Long codItemOrdemServico,
                                       final long novoKm) {
        final ChecklistOrdemServicoItemEntity entity = getItemOrdemServicoByCodigo(codItemOrdemServico)
                .toBuilder()
                .withKmColetadoVeiculoFechamentoItem(novoKm)
                .build();
        updateItemOrdemServico(entity);
    }
}
