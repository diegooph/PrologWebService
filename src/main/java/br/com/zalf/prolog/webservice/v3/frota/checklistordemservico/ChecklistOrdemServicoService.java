package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoProjection;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChecklistOrdemServicoService implements KmProcessoAtualizavel {
    @NotNull
    private final ChecklistOrdemServicoDao checklistOrdemServicoDao;
    @NotNull
    private final ChecklistOrdemServicoItemDao checklistOrdemServicoItemDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getItemOrdemServicoByCodigo(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmFechamentoItem(codProcesso, novoKm);
    }

    @Transactional
    public List<ChecklistOrdemServicoProjection> getOrdensServico(@NotNull final List<Long> codUnidades,
                                                                  @Nullable final Long codTipoVeiculo,
                                                                  @Nullable final String codVeiculo,
                                                                  @Nullable final StatusOrdemServico statusOrdemServico,
                                                                  final boolean incluirItensOrdemServico,
                                                                  final int limit,
                                                                  final int offset) {
        return checklistOrdemServicoDao.getOrdensServico(codUnidades,
                                                         codTipoVeiculo,
                                                         codVeiculo,
                                                         statusOrdemServico == null
                                                                 ? null
                                                                 : statusOrdemServico.asString(),
                                                         incluirItensOrdemServico,
                                                         limit,
                                                         offset);
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
