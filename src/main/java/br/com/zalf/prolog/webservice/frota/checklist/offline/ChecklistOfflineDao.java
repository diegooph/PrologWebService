package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ChecklistOfflineDao {

    @NotNull
    InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable;

    boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    Optional<TokenVersaoChecklist> getDadosAtuaisUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<ModeloChecklistOffline> getModelosChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<ColaboradorChecklistOffline> getColaboradoresChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<VeiculoChecklistOffline> getVeiculosChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    UnidadeChecklistOffline getUnidadeChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    boolean verifyIfTokenChecklistExists(@NotNull final String tokenSincronizacao) throws Throwable;
}
