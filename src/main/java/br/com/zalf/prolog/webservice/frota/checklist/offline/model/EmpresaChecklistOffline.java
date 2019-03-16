package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class EmpresaChecklistOffline {
    /**
     * Código único de identificação da Empresa.
     */
    @NotNull
    private final Long codEmpresa;

    /**
     * Atributo alfanumérico que representa o nome da Empresa.
     */
    @NotNull
    private final String nomeEmpresa;

    /**
     * Código único de identificação da Regional.
     */
    @NotNull
    private final Long codRegional;

    /**
     * Atributo alfanumérico que representa o nome da Regional.
     */
    @NotNull
    private final String nomeRegional;

    /**
     * Código único de identificação da Unidade.
     */
    @NotNull
    private final Long codUnidade;

    /**
     * Atributo alfanumérico que representa o nome da Unidade.
     */
    @NotNull
    private final String nomeUnidade;

    public EmpresaChecklistOffline(@NotNull final Long codEmpresa,
                                   @NotNull final String nomeEmpresa,
                                   @NotNull final Long codRegional,
                                   @NotNull final String nomeRegional,
                                   @NotNull final Long codUnidade,
                                   @NotNull final String nomeUnidade) {
        this.codEmpresa = codEmpresa;
        this.nomeEmpresa = nomeEmpresa;
        this.codRegional = codRegional;
        this.nomeRegional = nomeRegional;
        this.codUnidade = codUnidade;
        this.nomeUnidade = nomeUnidade;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    @NotNull
    public Long getCodRegional() {
        return codRegional;
    }

    @NotNull
    public String getNomeRegional() {
        return nomeRegional;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }
}
