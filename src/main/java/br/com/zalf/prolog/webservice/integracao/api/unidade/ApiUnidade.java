package br.com.zalf.prolog.webservice.integracao.api.unidade;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiUnidade {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Boolean statusAtiva;

    ApiUnidade(@NotNull final Long codEmpresa,
               @NotNull final Long codigo,
               @NotNull final String nome,
               @NotNull final Boolean statusAtiva) {
        this.codEmpresa = codEmpresa;
        this.codigo = codigo;
        this.nome = nome;
        this.statusAtiva = statusAtiva;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public Boolean getStatusAtiva() {
        return statusAtiva;
    }
}
