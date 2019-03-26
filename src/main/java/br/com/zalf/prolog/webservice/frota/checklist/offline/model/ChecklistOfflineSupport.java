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
    public static final String HEADER_VERSAO_DADOS_CHECKLIST = "ProLog-Versao-Dados-Checklist";
    @NotNull
    public static final String HEADER_TOKEN_CHECKLIST = "ProLog-Token-Checklist-Offline";

    /**
     * Código da Unidade a qual os dados presentes neste objeto pertencem.
     */
    @NotNull
    private final Long codUnidadeDados;

    /**
     * Objeto que contém as informações do checklist offline de acordo com os estados dos dados.
     * Esses estados podem ser:
     * <p>
     * * {@link EstadoChecklistOfflineSupport#ATUALIZADO}
     * * {@link EstadoChecklistOfflineSupport#ATUALIZACAO_FORCADA}
     * * {@link EstadoChecklistOfflineSupport#DESATUALIZADO}
     * * {@link EstadoChecklistOfflineSupport#SEM_DADOS}
     */
    @NotNull
    private final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport;

    public ChecklistOfflineSupport(@NotNull final Long codUnidadeDados,
                                   @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport) {
        this.codUnidadeDados = codUnidadeDados;
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<ChecklistOfflineSupport> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ChecklistOfflineSupport.class, "estadoChecklistOfflineSupport")
                .registerSubtype(ChecklistOfflineSupportAtualizado.class, ATUALIZADO.asString())
                .registerSubtype(ChecklistOfflineSupportAtualizacaoForcada.class, ATUALIZACAO_FORCADA.asString())
                .registerSubtype(ChecklistOfflineSupportDesatualizado.class, DESATUALIZADO.asString())
                .registerSubtype(ChecklistOfflineSupportSemDados.class, SEM_DADOS.asString());
    }

    @NotNull
    public Long getCodUnidadeDados() {
        return codUnidadeDados;
    }

    @NotNull
    public EstadoChecklistOfflineSupport getEstadoChecklistOfflineSupport() {
        return estadoChecklistOfflineSupport;
    }
}