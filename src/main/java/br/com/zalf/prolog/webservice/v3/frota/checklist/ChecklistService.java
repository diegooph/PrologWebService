package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistGetDto;
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
public class ChecklistService implements KmProcessoAtualizavel {
    private static final String TAG = ChecklistService.class.getSimpleName();
    @NotNull
    private final ChecklistDao checklistDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getByCodigo(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmColetado(codProcesso, novoKm);
    }

    @NotNull
    public ChecklistEntity getByCodigo(@NotNull final Long codigo) {
        return checklistDao.getOne(codigo);
    }

    public void update(@NotNull final ChecklistEntity checklistEntity) {
        checklistDao.save(checklistEntity);
    }

    @Transactional
    public void updateKmColetado(@NotNull final Long codChecklist,
                                 final long novoKm) {
        final ChecklistEntity entity = getByCodigo(codChecklist)
                .toBuilder()
                .withKmColetadoVeiculo(novoKm)
                .build();
        update(entity);
    }

    public List<ChecklistGetDto> getChecklists(@NotNull final List<Long> codUnidades,
                                               @Nullable final Long codColaborador,
                                               @Nullable final Long codTipoVeiculo,
                                               @Nullable final Long codVeiculo,
                                               final boolean incluirRespostas,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal,
                                               final int limit,
                                               final long offset) {
        try {
            return checklistDao.getChecklists(codUnidades,
                                              codColaborador,
                                              codTipoVeiculo,
                                              codVeiculo,
                                              incluirRespostas,
                                              dataInicial,
                                              dataFinal,
                                              limit,
                                              offset);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar lista de checklists das unidades.Código das Unidades: %d\n",
                                     codUnidades), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar listagem de checklists, tente novamente.");
        }
    }
}
