package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 16/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class DadosChecklistOfflineUnidade {
    /**
     * Código da unidade a qual os demais atributos pertencem.
     */
    @NotNull
    private final Long codUnidade;

    /**
     * Atributo utilizado para guardar a versão em que os dados de realização de checklist offline se encontram,
     * para uma unidade específica.
     * Este atributo é <code>NULL</code> para o caso em que a unidade não possui nenhuma versão de dados configurada.
     */
    @Nullable
    private Long versaoDadosBanco;

    /**
     * Atributo para salvar o Token que a unidade utiliza para sincronizar os checklists realizados de forma offline.
     * Este atributo é <code>NULL</code> para o caso em que a unidade não possui nenhum token.
     */
    @Nullable
    private String tokenSincronizacaoChecklist;

    /**
     * Estado em que os dados do checklist offline se encontram.
     * Esses estados podem ser:
     * <p>
     * * {@link EstadoChecklistOfflineSupport#ATUALIZADO}
     * * {@link EstadoChecklistOfflineSupport#ATUALIZACAO_FORCADA}
     * * {@link EstadoChecklistOfflineSupport#DESATUALIZADO}
     */
    @NotNull
    private EstadoChecklistOfflineSupport estadoChecklistOfflineSupport;

    public DadosChecklistOfflineUnidade(@NotNull final Long codUnidade,
                                        @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport) {
        this.codUnidade = codUnidade;
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
    }

    public DadosChecklistOfflineUnidade(@NotNull final Long codUnidade,
                                        @NotNull final Long versaoDadosBanco,
                                        @NotNull final String tokenSincronizacaoChecklist,
                                        @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport) {
        this.codUnidade = codUnidade;
        this.versaoDadosBanco = versaoDadosBanco;
        this.tokenSincronizacaoChecklist = tokenSincronizacaoChecklist;
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @Nullable
    public Long getVersaoDadosBanco() {
        return versaoDadosBanco;
    }

    public void setVersaoDadosBanco(@Nullable final Long versaoDadosBanco) {
        this.versaoDadosBanco = versaoDadosBanco;
    }

    @Nullable
    public String getTokenSincronizacaoChecklist() {
        return tokenSincronizacaoChecklist;
    }

    public void setTokenSincronizacaoChecklist(@Nullable final String tokenSincronizacaoChecklist) {
        this.tokenSincronizacaoChecklist = tokenSincronizacaoChecklist;
    }

    @NotNull
    public EstadoChecklistOfflineSupport getEstadoChecklistOfflineSupport() {
        return estadoChecklistOfflineSupport;
    }

    public void setEstadoChecklistOfflineSupport(
            @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport) {
        this.estadoChecklistOfflineSupport = estadoChecklistOfflineSupport;
    }
}
