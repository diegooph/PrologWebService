package br.com.zalf.prolog.webservice.frota.checklist.offline.model;


import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistOfflineData {
    /**
     * Valor alfanumérico utilizado para a sincronização de dados do checklist offline para a unidade.
     */
    @NotNull
    private final String tokenSincronizacaoDadosUnidade;

    /**
     * Versão que os dados da unidade possuem. A versão é incrementada sempre que alguma configuração que impacta a
     * realização do checklist offline é alterada.
     */
    @NotNull
    private final Long versaoDadosUnidadeChecklist;

    /**
     * {@link ModeloChecklistOffline Modelos de checklist} disponíveis para serem realizados de forma offline.
     */
    @NotNull
    private final List<ModeloChecklistOffline> modelosChecklistsDisponiveis;

    /**
     * {@link ColaboradorChecklistOffline Colaboradores} que possuem acesso ao checklist offline.
     */
    @NotNull
    private final List<ColaboradorChecklistOffline> colaboradoresChecklistOffline;

    /**
     * {@link VeiculoChecklistOffline Veículos} disponíveis para realizar checklist offline.
     */
    @NotNull
    private final List<VeiculoChecklistOffline> veiculosChecklistOffline;

    /**
     * Informações da Empresa a qual os dados do checklist se referem.
     */
    @NotNull
    private final EmpresaChecklistOffline empresaChecklistOffline;

    public ChecklistOfflineData(@NotNull final String tokenSincronizacaoDadosUnidade,
                                @NotNull final Long versaoDadosUnidadeChecklist,
                                @NotNull final List<ModeloChecklistOffline> modelosChecklistsDisponiveis,
                                @NotNull final List<ColaboradorChecklistOffline> colaboradoresChecklistOffline,
                                @NotNull final List<VeiculoChecklistOffline> veiculosChecklistOffline,
                                @NotNull final EmpresaChecklistOffline empresaChecklistOffline) {
        this.tokenSincronizacaoDadosUnidade = tokenSincronizacaoDadosUnidade;
        this.versaoDadosUnidadeChecklist = versaoDadosUnidadeChecklist;
        this.modelosChecklistsDisponiveis = modelosChecklistsDisponiveis;
        this.colaboradoresChecklistOffline = colaboradoresChecklistOffline;
        this.veiculosChecklistOffline = veiculosChecklistOffline;
        this.empresaChecklistOffline = empresaChecklistOffline;
    }

    @NotNull
    public String getTokenSincronizacaoDadosUnidade() {
        return tokenSincronizacaoDadosUnidade;
    }

    @NotNull
    public Long getVersaoDadosUnidadeChecklist() {
        return versaoDadosUnidadeChecklist;
    }

    @NotNull
    public List<ModeloChecklistOffline> getModelosChecklistsDisponiveis() {
        return modelosChecklistsDisponiveis;
    }

    @NotNull
    public List<ColaboradorChecklistOffline> getColaboradoresChecklistOffline() {
        return colaboradoresChecklistOffline;
    }

    @NotNull
    public List<VeiculoChecklistOffline> getVeiculosChecklistOffline() {
        return veiculosChecklistOffline;
    }

    @NotNull
    public EmpresaChecklistOffline getEmpresaChecklistOffline() {
        return empresaChecklistOffline;
    }
}