package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistOfflineSupportDesatualizado extends ChecklistOfflineSupport {
    @NotNull
    private final String tokenSincronizacaoDadosUnidade;
    @NotNull
    private final Long versaoDadosUnidadeChecklist;
    @NotNull
    private final List<ModeloChecklistOffline> modelosChecklistsDisponiveis;

    public ChecklistOfflineSupportDesatualizado(
            @NotNull final Long codUnidadeDados,
            @NotNull final String tokenSincronizacaoDadosUnidade,
            @NotNull final Long versaoDadosUnidadeChecklist,
            @NotNull final List<ModeloChecklistOffline> modelosChecklistsDisponiveis) {
        super(codUnidadeDados, EstadoChecklistOfflineSupport.DESATUALIZADO);
        this.tokenSincronizacaoDadosUnidade = tokenSincronizacaoDadosUnidade;
        this.versaoDadosUnidadeChecklist = versaoDadosUnidadeChecklist;
        this.modelosChecklistsDisponiveis = modelosChecklistsDisponiveis;
    }
}
