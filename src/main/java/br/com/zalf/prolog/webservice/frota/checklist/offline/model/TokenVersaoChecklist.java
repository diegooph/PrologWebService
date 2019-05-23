package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 08/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TokenVersaoChecklist {
    @NotNull
    private final Long codUnidadeProLog;
    @NotNull
    private final Long versaoDadosBanco;
    @NotNull
    private final String tokenSincronizacao;

    public TokenVersaoChecklist(@NotNull final Long codUnidade,
                                @NotNull final Long versaoDados,
                                @NotNull final String tokenSincronizacao) {
        this.codUnidadeProLog = codUnidade;
        this.versaoDadosBanco = versaoDados;
        this.tokenSincronizacao = tokenSincronizacao;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidadeProLog;
    }

    @NotNull
    public Long getVersaoDados() {
        return versaoDadosBanco;
    }

    @NotNull
    public String getTokenSincronizacao() {
        return tokenSincronizacao;
    }
}