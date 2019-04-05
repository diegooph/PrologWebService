package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class ChecklistOfflineSupport {
    @NotNull
    public static final String HEADER_VERSAO_DADOS_CHECKLIST = "ProLog-Versao-Dados-Checklist";
    @NotNull
    public static final String HEADER_TOKEN_CHECKLIST = "ProLog-Token-Checklist-Offline";

    @NotNull
    private final Long codUnidade;

    @NotNull
    private final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport;

    private final boolean foiAtualizacaoForacada;

    @NotNull
    @Exclude
    private final String tipo;

    public ChecklistOfflineSupport(@NotNull final String tipo,
                                   @NotNull final Long codUnidade,
                                   @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport,
                                   final boolean foiAtualizacaoForacada) {
        this.codUnidade = codUnidade;
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
        this.tipo = tipo;
        this.foiAtualizacaoForacada = foiAtualizacaoForacada;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<ChecklistOfflineSupport> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ChecklistOfflineSupport.class, "tipo")
                .registerSubtype(ChecklistOfflineSupportSemDados.class, ChecklistOfflineSupportSemDados.SEM_DADOS)
                .registerSubtype(ChecklistOfflineSupportComDados.class, ChecklistOfflineSupportComDados.COM_DADOS);
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public EstadoChecklistOfflineSupport getEstadoChecklistOfflineSupport() {
        return estadoChecklistOfflineSupport;
    }

    public boolean isFoiAtualizacaoForacada() {
        return foiAtualizacaoForacada;
    }

    @NotNull
    public String getTipo() {
        return tipo;
    }
}
