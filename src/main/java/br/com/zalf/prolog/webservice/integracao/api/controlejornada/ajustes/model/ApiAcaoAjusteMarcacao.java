package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum ApiAcaoAjusteMarcacao {
    ADICAO("ADICAO"),
    EDICAO("EDICAO"),
    ATIVACAO("ATIVACAO"),
    INATIVACAO("INATIVACAO"),
    ADICAO_INICIO_FIM("ADICAO_INICIO_FIM");

    @NotNull
    private final String acaoAjuste;

    ApiAcaoAjusteMarcacao(@NotNull final String acaoAjuste) {
        this.acaoAjuste = acaoAjuste;
    }

    @NotNull
    public String asString() {
        return acaoAjuste;
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static ApiAcaoAjusteMarcacao fromString(@NotNull final String acaoAjuste) {
        Preconditions.checkNotNull(acaoAjuste, "tipoMarcacaoAjuste cannot be null!");

        for (final ApiAcaoAjusteMarcacao acao : ApiAcaoAjusteMarcacao.values()) {
            if (acaoAjuste.equals(acao.acaoAjuste)) {
                return acao;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de ação de ajuste encontrado com o nome: " + acaoAjuste);
    }
}
