package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemFiltro;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistProjection;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public List<ChecklistProjection> getChecklistsListagem(
            @NotNull final ChecklistListagemFiltro checklistListagemFiltro) {
        try {
            return checklistDao.getChecklistsListagem(checklistListagemFiltro.getCodUnidades(),
                                                      checklistListagemFiltro.getDataInicial(),
                                                      checklistListagemFiltro.getDataFinal(),
                                                      checklistListagemFiltro.getCodColaborador(),
                                                      checklistListagemFiltro.getCodVeiculo(),
                                                      checklistListagemFiltro.getCodTipoVeiculo(),
                                                      checklistListagemFiltro.isIncluirRespostas(),
                                                      checklistListagemFiltro.getLimit(),
                                                      checklistListagemFiltro.getOffset());
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar lista de checklists das unidades.\n" +
                                             "codUnidades: %s\n" +
                                             "codColaborador: %s\n" +
                                             "codTipoVeiculo: %s\n" +
                                             "codVeiculo: %s\n" +
                                             "isIncluirRespostas: %s\n" +
                                             "dataInicial: %s\n" +
                                             "dataFinal: %s\n" +
                                             "limit: %s\n" +
                                             "offset: %s\n",
                                     checklistListagemFiltro.getCodUnidades().toString(),
                                     checklistListagemFiltro.getCodColaborador(),
                                     checklistListagemFiltro.getCodTipoVeiculo(),
                                     checklistListagemFiltro.getCodVeiculo(),
                                     checklistListagemFiltro.isIncluirRespostas(),
                                     checklistListagemFiltro.getDataInicial(),
                                     checklistListagemFiltro.getDataFinal(),
                                     checklistListagemFiltro.getLimit(),
                                     checklistListagemFiltro.getOffset()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar listagem de checklists, tente novamente.");
        }
    }
}
