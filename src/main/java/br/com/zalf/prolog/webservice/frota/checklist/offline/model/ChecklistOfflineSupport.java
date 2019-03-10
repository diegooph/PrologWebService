package br.com.zalf.prolog.webservice.frota.checklist.offline.model;


import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import static br.com.zalf.prolog.webservice.frota.checklist.offline.model.EstadoChecklistOfflineSupport.*;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class ChecklistOfflineSupport {
    @NotNull
    public static final String HEADER_NAME_VERSAO_DADOS_CHECKLIST = "ProLog-Versao-Dados-Checklist";
    @NotNull
    private final Long codUnidadeDados;
    @NotNull
    private final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport;

    public ChecklistOfflineSupport(@NotNull final Long codUnidadeDados,
                                   @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport) {
        this.codUnidadeDados = codUnidadeDados;
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
    }

    public static RuntimeTypeAdapterFactory<ChecklistOfflineSupport> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ChecklistOfflineSupport.class, "estadoChecklistOfflineSupport")
                .registerSubtype(ChecklistOfflineSupportAtualizado.class, ATUALIZADO.asString())
                .registerSubtype(ChecklistOfflineSupportAtualizacaoForcada.class, ATUALIZACAO_FORCADA.asString())
                .registerSubtype(ChecklistOfflineSupportDesatualizado.class, DESATUALIZADO.asString())
                .registerSubtype(ChecklistOfflineSupportSemDados.class, SEM_DADOS.asString());
    }
}